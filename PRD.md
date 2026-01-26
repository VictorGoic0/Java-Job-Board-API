# Product Requirements Document: Job Board API

## Project Overview

A production-ready RESTful API for a job board platform built with Spring Boot and Java. The system manages job postings with full CRUD operations, advanced search capabilities, and proper database relationships. Designed to handle real-world concurrency scenarios with optimistic locking and comprehensive validation.

---

## Tech Stack

- **Language**: Java 17+
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL (Dockerized)
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Containerization**: Docker + Docker Compose
- **Testing**: JUnit 5, Mockito, MockMvc

---

## Database Schema

### Company Table
```sql
CREATE TABLE company (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    website VARCHAR(255),
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0  -- For optimistic locking
);
```

### Job Table
```sql
CREATE TABLE job (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    company_id BIGINT NOT NULL,
    location VARCHAR(255) NOT NULL,
    salary_min DECIMAL(10, 2),
    salary_max DECIMAL(10, 2),
    job_type VARCHAR(50) NOT NULL,  -- FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP
    experience_level VARCHAR(50) NOT NULL,  -- ENTRY, MID, SENIOR
    remote_option VARCHAR(50) NOT NULL,  -- REMOTE, HYBRID, ONSITE
    posted_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    application_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,  -- For optimistic locking
    FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
);

CREATE INDEX idx_job_company_id ON job(company_id);
CREATE INDEX idx_job_is_active ON job(is_active);
CREATE INDEX idx_job_posted_date ON job(posted_date);
```

---

## Entity Models

### Company Entity
```java
@Entity
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Company name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Pattern(regexp = "^(https?://).*", message = "Website must be a valid URL")
    private String website;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs = new ArrayList<>();
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Integer version;
    
    // Getters, setters, constructors
}
```

### Job Entity
```java
@Entity
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @NotNull(message = "Company is required")
    private Company company;
    
    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;
    
    @DecimalMin(value = "0.0", message = "Minimum salary must be positive")
    @Column(name = "salary_min", precision = 10, scale = 2)
    private BigDecimal salaryMin;
    
    @DecimalMin(value = "0.0", message = "Maximum salary must be positive")
    @Column(name = "salary_max", precision = 10, scale = 2)
    private BigDecimal salaryMax;
    
    @NotNull(message = "Job type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;
    
    @NotNull(message = "Experience level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false)
    private ExperienceLevel experienceLevel;
    
    @NotNull(message = "Remote option is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "remote_option", nullable = false)
    private RemoteOption remoteOption;
    
    @Column(name = "posted_date", nullable = false)
    private LocalDateTime postedDate;
    
    @Future(message = "Expiry date must be in the future")
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Pattern(regexp = "^(https?://).*", message = "Application URL must be valid")
    @Column(name = "application_url", length = 500)
    private String applicationUrl;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Integer version;
    
    @PrePersist
    protected void onCreate() {
        postedDate = LocalDateTime.now();
    }
    
    // Getters, setters, constructors
}
```

### Enums
```java
public enum JobType {
    FULL_TIME,
    PART_TIME,
    CONTRACT,
    INTERNSHIP
}

public enum ExperienceLevel {
    ENTRY,
    MID,
    SENIOR
}

public enum RemoteOption {
    REMOTE,
    HYBRID,
    ONSITE
}
```

---

## Phase I: Core Setup & Basic CRUD

### Objectives
- Set up Spring Boot project with PostgreSQL database
- Implement basic CRUD operations for Jobs and Companies
- Add validation and error handling
- Implement concurrency control with optimistic locking

### Deliverables

#### 1. Project Setup
- [ ] Initialize Spring Boot project with Maven
- [ ] Add dependencies: Spring Web, Spring Data JPA, PostgreSQL driver, Validation, Lombok
- [ ] Create `docker-compose.yml` for PostgreSQL
- [ ] Configure `application.properties` / `application.yml`

**docker-compose.yml**:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: job_board_db
    environment:
      POSTGRES_DB: job_board
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

**application.yml**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/job_board
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  jackson:
    serialization:
      write-dates-as-timestamps: false

server:
  port: 8080

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

#### 2. API Endpoints - Company

**Base URL**: `/api/companies`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/companies` | Get all companies | - | `200 OK` + List<CompanyDTO> |
| GET | `/api/companies/{id}` | Get company by ID | - | `200 OK` + CompanyDTO |
| POST | `/api/companies` | Create new company | CompanyCreateDTO | `201 Created` + CompanyDTO |
| PATCH | `/api/companies/{id}` | Update company | CompanyUpdateDTO | `200 OK` + CompanyDTO |
| DELETE | `/api/companies/{id}` | Delete company | - | `204 No Content` |

#### 3. API Endpoints - Job

**Base URL**: `/api/jobs`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/jobs` | Get all jobs (includes company data via join) | - | `200 OK` + List<JobDTO> |
| GET | `/api/jobs/{id}` | Get job by ID with full details | - | `200 OK` + JobDetailDTO |
| POST | `/api/jobs` | Create new job | JobCreateDTO | `201 Created` + JobDTO |
| PATCH | `/api/jobs/{id}` | Update specific job fields | JobUpdateDTO | `200 OK` + JobDTO |
| DELETE | `/api/jobs/{id}` | Delete job | - | `204 No Content` |

#### 4. DTOs

**JobDTO** (for list responses):
```java
public class JobDTO {
    private Long id;
    private String title;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private RemoteOption remoteOption;
    private LocalDateTime postedDate;
    private Boolean isActive;
    
    // Embedded company info (from join)
    private CompanySummaryDTO company;
}
```

**JobDetailDTO** (for single job response with full details):
```java
public class JobDetailDTO extends JobDTO {
    private String description;
    private LocalDateTime expiryDate;
    private String applicationUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**JobCreateDTO**:
```java
public class JobCreateDTO {
    @NotBlank
    private String title;
    
    @NotBlank
    private String description;
    
    @NotNull
    private Long companyId;
    
    @NotBlank
    private String location;
    
    @DecimalMin("0.0")
    private BigDecimal salaryMin;
    
    @DecimalMin("0.0")
    private BigDecimal salaryMax;
    
    @NotNull
    private JobType jobType;
    
    @NotNull
    private ExperienceLevel experienceLevel;
    
    @NotNull
    private RemoteOption remoteOption;
    
    @Future
    private LocalDateTime expiryDate;
    
    @Pattern(regexp = "^(https?://).*")
    private String applicationUrl;
}
```

**JobUpdateDTO** (all fields optional for PATCH):
```java
public class JobUpdateDTO {
    private String title;
    private String description;
    private Long companyId;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private RemoteOption remoteOption;
    private LocalDateTime expiryDate;
    private String applicationUrl;
}
```

**CompanyDTO**, **CompanyCreateDTO**, **CompanyUpdateDTO** (similar structure)

#### 5. Repository Layer

```java
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);
}

public interface JobRepository extends JpaRepository<Job, Long> {
    // Basic queries for Phase I
    List<Job> findByCompanyId(Long companyId);
    
    // Will add more custom queries in Phase II
}
```

#### 6. Service Layer

**JobService** key methods:
```java
@Service
@Transactional
public class JobService {
    
    @Transactional(readOnly = true)
    public List<JobDTO> getAllJobs() {
        // Fetch jobs with company data using join fetch to avoid N+1
    }
    
    @Transactional(readOnly = true)
    public JobDetailDTO getJobById(Long id) {
        // Fetch single job with all details
    }
    
    public JobDTO createJob(JobCreateDTO dto) {
        // Validate company exists
        // Create and save job
        // Return DTO
    }
    
    public JobDTO updateJob(Long id, JobUpdateDTO dto) {
        // Fetch existing job (optimistic locking)
        // Update only provided fields
        // Save and return DTO
    }
    
    public void deleteJob(Long id) {
        // Hard delete
    }
}
```

#### 7. Controller Layer

```java
@RestController
@RequestMapping("/api/jobs")
@Validated
public class JobController {
    
    private final JobService jobService;
    
    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs() { }
    
    @GetMapping("/{id}")
    public ResponseEntity<JobDetailDTO> getJobById(@PathVariable Long id) { }
    
    @PostMapping
    public ResponseEntity<JobDTO> createJob(@Valid @RequestBody JobCreateDTO dto) { }
    
    @PatchMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(
        @PathVariable Long id,
        @Valid @RequestBody JobUpdateDTO dto
    ) { }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) { }
}
```

#### 8. Exception Handling

**Custom Exceptions**:
```java
public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(Long id) {
        super("Job not found with id: " + id);
    }
}

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(Long id) {
        super("Company not found with id: " + id);
    }
}

public class InvalidJobDataException extends RuntimeException {
    public InvalidJobDataException(String message) {
        super(message);
    }
}

public class OptimisticLockException extends RuntimeException {
    public OptimisticLockException(String message) {
        super(message);
    }
}
```

**Global Exception Handler**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleJobNotFound(JobNotFoundException ex) {
        return new ErrorResponse(
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now()
        );
    }
    
    @ExceptionHandler(CompanyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCompanyNotFound(CompanyNotFoundException ex) {
        return new ErrorResponse(
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now()
        );
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ValidationErrorResponse(
            "Validation failed",
            HttpStatus.BAD_REQUEST.value(),
            errors,
            LocalDateTime.now()
        );
    }
    
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return new ErrorResponse(
            "The resource was modified by another user. Please refresh and try again.",
            HttpStatus.CONFLICT.value(),
            LocalDateTime.now()
        );
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneral(Exception ex) {
        return new ErrorResponse(
            "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now()
        );
    }
}
```

**Error Response DTOs**:
```java
public class ErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    // Constructor, getters, setters
}

public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;
    // Constructor, getters, setters
}
```

#### 9. Concurrency Control

- Use `@Version` annotation on both Job and Company entities
- Spring Data JPA automatically handles optimistic locking
- On concurrent updates, throws `ObjectOptimisticLockingFailureException`
- Global exception handler catches and returns 409 Conflict

**Example scenario**:
```
User A fetches Job (version=1)
User B fetches Job (version=1)
User A updates Job -> version becomes 2
User B tries to update Job with version=1 -> Exception thrown
```

#### 10. Validation Rules

**Job Validation**:
- Title, description, location: Required, not blank
- Company ID: Required, must reference existing company
- Salary min/max: If provided, must be >= 0
- Salary max must be >= salary min (custom validator)
- Job type, experience level, remote option: Required, valid enum
- Expiry date: If provided, must be future date
- Application URL: If provided, must be valid URL format
- Is active: Defaults to true

**Company Validation**:
- Name: Required, not blank
- Location: Required
- Website: If provided, must be valid URL

#### Phase I Acceptance Criteria
- [ ] Docker Compose successfully starts PostgreSQL
- [ ] Spring Boot application connects to database
- [ ] All 5 CRUD endpoints work for both Job and Company
- [ ] GET /api/jobs returns jobs with joined company data
- [ ] GET /api/jobs/{id} returns full job details with company
- [ ] POST creates job with valid company_id foreign key
- [ ] PATCH updates only provided fields
- [ ] DELETE removes job from database
- [ ] Validation errors return 400 with field-specific messages
- [ ] Not found errors return 404 with clear message
- [ ] Concurrent updates properly handled with 409 response
- [ ] Unknown errors return 500 with generic message
- [ ] Database relationships enforce referential integrity
- [ ] Timestamps (created_at, updated_at) auto-populate

---

## Phase II: Advanced Routes

### Objectives
- Add search and filtering capabilities
- Implement pagination
- Add route for active jobs only
- Add soft delete (deactivate) functionality

### Deliverables

#### 1. Search & Filter Endpoint

**Endpoint**: `GET /api/jobs/search`

**Query Parameters**:
- `keyword` (optional): Search in title and description
- `location` (optional): Filter by location (case-insensitive, partial match)
- `companyId` (optional): Filter by company
- `jobType` (optional): Filter by job type enum
- `experienceLevel` (optional): Filter by experience level
- `remoteOption` (optional): Filter by remote option
- `minSalary` (optional): Minimum salary filter
- `maxSalary` (optional): Maximum salary filter
- `isActive` (optional): Filter by active status (default true)
- `page` (optional): Page number (0-indexed, default 0)
- `size` (optional): Page size (default 20, max 100)
- `sort` (optional): Sort field (default: postedDate,desc)

**Example Requests**:
```
GET /api/jobs/search?keyword=java&location=New York&remoteOption=REMOTE
GET /api/jobs/search?experienceLevel=SENIOR&minSalary=100000&page=0&size=10
GET /api/jobs/search?companyId=5&isActive=true&sort=salaryMax,desc
```

**Response**:
```json
{
  "content": [
    {
      "id": 1,
      "title": "Senior Java Developer",
      "location": "New York, NY",
      "salaryMin": 120000,
      "salaryMax": 160000,
      "jobType": "FULL_TIME",
      "experienceLevel": "SENIOR",
      "remoteOption": "REMOTE",
      "postedDate": "2024-01-15T10:30:00",
      "isActive": true,
      "company": {
        "id": 5,
        "name": "Tech Corp",
        "location": "New York, NY"
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalElements": 45,
  "totalPages": 3,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 20
}
```

**Repository Method**:
```java
public interface JobRepository extends JpaRepository<Job, Long> {
    
    @Query("SELECT j FROM Job j WHERE " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:companyId IS NULL OR j.company.id = :companyId) " +
           "AND (:jobType IS NULL OR j.jobType = :jobType) " +
           "AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) " +
           "AND (:remoteOption IS NULL OR j.remoteOption = :remoteOption) " +
           "AND (:minSalary IS NULL OR j.salaryMax >= :minSalary) " +
           "AND (:maxSalary IS NULL OR j.salaryMin <= :maxSalary) " +
           "AND (:isActive IS NULL OR j.isActive = :isActive)")
    Page<Job> searchJobs(
        @Param("keyword") String keyword,
        @Param("location") String location,
        @Param("companyId") Long companyId,
        @Param("jobType") JobType jobType,
        @Param("experienceLevel") ExperienceLevel experienceLevel,
        @Param("remoteOption") RemoteOption remoteOption,
        @Param("minSalary") BigDecimal minSalary,
        @Param("maxSalary") BigDecimal maxSalary,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
}
```

#### 2. Active Jobs Endpoint

**Endpoint**: `GET /api/jobs/active`

Returns only jobs where:
- `is_active = true`
- `expiry_date IS NULL OR expiry_date > NOW()`

Supports same pagination parameters as search endpoint.

**Repository Method**:
```java
@Query("SELECT j FROM Job j WHERE j.isActive = true " +
       "AND (j.expiryDate IS NULL OR j.expiryDate > CURRENT_TIMESTAMP)")
Page<Job> findActiveJobs(Pageable pageable);
```

#### 3. Deactivate Job Endpoint

**Endpoint**: `POST /api/jobs/{id}/deactivate`

Soft delete - sets `is_active = false` instead of deleting the record.

**Request**: No body required

**Response**: 
```json
{
  "message": "Job successfully deactivated",
  "jobId": 123
}
```

**Service Method**:
```java
@Transactional
public void deactivateJob(Long id) {
    Job job = jobRepository.findById(id)
        .orElseThrow(() -> new JobNotFoundException(id));
    job.setIsActive(false);
    jobRepository.save(job);
}
```

#### 4. Pagination on Existing Endpoints

Update existing endpoints to support pagination:

**GET /api/jobs** - Add pagination:
```java
@GetMapping
public ResponseEntity<Page<JobDTO>> getAllJobs(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(defaultValue = "postedDate,desc") String sort
) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
    Page<JobDTO> jobs = jobService.getAllJobs(pageable);
    return ResponseEntity.ok(jobs);
}
```

**GET /api/companies** - Add pagination similarly.

#### 5. Additional Repository Queries

```java
public interface JobRepository extends JpaRepository<Job, Long> {
    
    // Existing from Phase I
    List<Job> findByCompanyId(Long companyId);
    
    // Phase II additions
    Page<Job> findAll(Pageable pageable);
    
    Page<Job> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.isActive = true " +
           "AND (j.expiryDate IS NULL OR j.expiryDate > CURRENT_TIMESTAMP)")
    Page<Job> findActiveJobs(Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:companyId IS NULL OR j.company.id = :companyId) " +
           "AND (:jobType IS NULL OR j.jobType = :jobType) " +
           "AND (:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) " +
           "AND (:remoteOption IS NULL OR j.remoteOption = :remoteOption) " +
           "AND (:minSalary IS NULL OR j.salaryMax >= :minSalary) " +
           "AND (:maxSalary IS NULL OR j.maxSalary <= :maxSalary) " +
           "AND (:isActive IS NULL OR j.isActive = :isActive)")
    Page<Job> searchJobs(
        @Param("keyword") String keyword,
        @Param("location") String location,
        @Param("companyId") Long companyId,
        @Param("jobType") JobType jobType,
        @Param("experienceLevel") ExperienceLevel experienceLevel,
        @Param("remoteOption") RemoteOption remoteOption,
        @Param("minSalary") BigDecimal minSalary,
        @Param("maxSalary") BigDecimal maxSalary,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
}
```

#### Phase II Acceptance Criteria
- [ ] Search endpoint works with all filter combinations
- [ ] Search handles null/missing parameters correctly
- [ ] Active jobs endpoint returns only active, non-expired jobs
- [ ] Deactivate endpoint sets is_active to false
- [ ] Pagination works on all list endpoints
- [ ] Page size limits enforced (max 100)
- [ ] Sorting works on multiple fields
- [ ] Empty search results return empty page, not 404
- [ ] Invalid enum values return 400 with clear message

---

## Phase III: Testing

### Objectives
- Comprehensive test coverage for all layers
- Unit tests for service logic
- Integration tests for repository queries
- API tests for controller endpoints

### Deliverables

#### 1. Test Dependencies

Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

Create `application-test.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

#### 2. Unit Tests - Service Layer

**JobServiceTest.java**:
```java
@ExtendWith(MockitoExtension.class)
class JobServiceTest {
    
    @Mock
    private JobRepository jobRepository;
    
    @Mock
    private CompanyRepository companyRepository;
    
    @InjectMocks
    private JobService jobService;
    
    @Test
    void getAllJobs_ReturnsListOfJobs() {
        // Arrange
        Company company = new Company();
        company.setId(1L);
        company.setName("Tech Corp");
        
        Job job = new Job();
        job.setId(1L);
        job.setTitle("Java Developer");
        job.setCompany(company);
        
        Page<Job> jobPage = new PageImpl<>(List.of(job));
        when(jobRepository.findAll(any(Pageable.class))).thenReturn(jobPage);
        
        // Act
        Page<JobDTO> result = jobService.getAllJobs(PageRequest.of(0, 20));
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Java Developer", result.getContent().get(0).getTitle());
        verify(jobRepository).findAll(any(Pageable.class));
    }
    
    @Test
    void getJobById_JobExists_ReturnsJobDetailDTO() {
        // Arrange
        Company company = new Company();
        company.setId(1L);
        company.setName("Tech Corp");
        
        Job job = new Job();
        job.setId(1L);
        job.setTitle("Java Developer");
        job.setDescription("Build amazing things");
        job.setCompany(company);
        
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        
        // Act
        JobDetailDTO result = jobService.getJobById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
        assertEquals("Build amazing things", result.getDescription());
        verify(jobRepository).findById(1L);
    }
    
    @Test
    void getJobById_JobNotFound_ThrowsException() {
        // Arrange
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(JobNotFoundException.class, () -> {
            jobService.getJobById(999L);
        });
        verify(jobRepository).findById(999L);
    }
    
    @Test
    void createJob_ValidData_ReturnsCreatedJob() {
        // Arrange
        Company company = new Company();
        company.setId(1L);
        company.setName("Tech Corp");
        
        JobCreateDTO createDTO = new JobCreateDTO();
        createDTO.setTitle("Java Developer");
        createDTO.setDescription("Build amazing things");
        createDTO.setCompanyId(1L);
        createDTO.setLocation("New York");
        createDTO.setJobType(JobType.FULL_TIME);
        createDTO.setExperienceLevel(ExperienceLevel.SENIOR);
        createDTO.setRemoteOption(RemoteOption.REMOTE);
        
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArguments()[0]);
        
        // Act
        JobDTO result = jobService.createJob(createDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
        verify(companyRepository).findById(1L);
        verify(jobRepository).save(any(Job.class));
    }
    
    @Test
    void createJob_CompanyNotFound_ThrowsException() {
        // Arrange
        JobCreateDTO createDTO = new JobCreateDTO();
        createDTO.setCompanyId(999L);
        
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(CompanyNotFoundException.class, () -> {
            jobService.createJob(createDTO);
        });
        verify(companyRepository).findById(999L);
        verify(jobRepository, never()).save(any(Job.class));
    }
    
    @Test
    void updateJob_ValidData_ReturnsUpdatedJob() {
        // Arrange
        Company company = new Company();
        company.setId(1L);
        
        Job existingJob = new Job();
        existingJob.setId(1L);
        existingJob.setTitle("Old Title");
        existingJob.setCompany(company);
        existingJob.setVersion(0);
        
        JobUpdateDTO updateDTO = new JobUpdateDTO();
        updateDTO.setTitle("New Title");
        
        when(jobRepository.findById(1L)).thenReturn(Optional.of(existingJob));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArguments()[0]);
        
        // Act
        JobDTO result = jobService.updateJob(1L, updateDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        verify(jobRepository).findById(1L);
        verify(jobRepository).save(any(Job.class));
    }
    
    @Test
    void deleteJob_JobExists_DeletesSuccessfully() {
        // Arrange
        Job job = new Job();
        job.setId(1L);
        
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        doNothing().when(jobRepository).delete(job);
        
        // Act
        jobService.deleteJob(1L);
        
        // Assert
        verify(jobRepository).findById(1L);
        verify(jobRepository).delete(job);
    }
    
    @Test
    void deactivateJob_JobExists_SetsActiveToFalse() {
        // Arrange
        Job job = new Job();
        job.setId(1L);
        job.setIsActive(true);
        
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArguments()[0]);
        
        // Act
        jobService.deactivateJob(1L);
        
        // Assert
        assertFalse(job.getIsActive());
        verify(jobRepository).findById(1L);
        verify(jobRepository).save(job);
    }
}
```

**CompanyServiceTest.java** - Similar structure with company-specific tests

#### 3. Integration Tests - Repository Layer

**JobRepositoryTest.java**:
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class JobRepositoryTest {
    
    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    private Company testCompany;
    
    @BeforeEach
    void setUp() {
        testCompany = new Company();
        testCompany.setName("Test Company");
        testCompany.setLocation("Test Location");
        testCompany = companyRepository.save(testCompany);
    }
    
    @Test
    void findByCompanyId_ReturnsJobsForCompany() {
        // Arrange
        Job job1 = createJob("Job 1", testCompany);
        Job job2 = createJob("Job 2", testCompany);
        jobRepository.saveAll(List.of(job1, job2));
        
        Company otherCompany = new Company();
        otherCompany.setName("Other Company");
        otherCompany.setLocation("Other Location");
        otherCompany = companyRepository.save(otherCompany);
        
        Job job3 = createJob("Job 3", otherCompany);
        jobRepository.save(job3);
        
        entityManager.flush();
        entityManager.clear();
        
        // Act
        List<Job> result = jobRepository.findByCompanyId(testCompany.getId());
        
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(j -> j.getCompany().getId().equals(testCompany.getId())));
    }
    
    @Test
    void findActiveJobs_ReturnsOnlyActiveNonExpiredJobs() {
        // Arrange
        Job activeJob = createJob("Active Job", testCompany);
        activeJob.setIsActive(true);
        activeJob.setExpiryDate(null);
        
        Job inactiveJob = createJob("Inactive Job", testCompany);
        inactiveJob.setIsActive(false);
        
        Job expiredJob = createJob("Expired Job", testCompany);
        expiredJob.setIsActive(true);
        expiredJob.setExpiryDate(LocalDateTime.now().minusDays(1));
        
        Job futureExpiryJob = createJob("Future Expiry Job", testCompany);
        futureExpiryJob.setIsActive(true);
        futureExpiryJob.setExpiryDate(LocalDateTime.now().plusDays(30));
        
        jobRepository.saveAll(List.of(activeJob, inactiveJob, expiredJob, futureExpiryJob));
        entityManager.flush();
        
        // Act
        Page<Job> result = jobRepository.findActiveJobs(PageRequest.of(0, 10));
        
        // Assert
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(Job::getIsActive));
        assertTrue(result.getContent().stream()
            .allMatch(j -> j.getExpiryDate() == null || j.getExpiryDate().isAfter(LocalDateTime.now())));
    }
    
    @Test
    void searchJobs_WithKeyword_ReturnsMatchingJobs() {
        // Arrange
        Job javaJob = createJob("Senior Java Developer", testCompany);
        javaJob.setDescription("Looking for experienced Java developer");
        
        Job pythonJob = createJob("Python Engineer", testCompany);
        pythonJob.setDescription("Python experience required");
        
        jobRepository.saveAll(List.of(javaJob, pythonJob));
        entityManager.flush();
        
        // Act
        Page<Job> result = jobRepository.searchJobs(
            "java", null, null, null, null, null, null, null, true,
            PageRequest.of(0, 10)
        );
        
        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Senior Java Developer", result.getContent().get(0).getTitle());
    }
    
    @Test
    void searchJobs_WithLocation_ReturnsMatchingJobs() {
        // Arrange
        Job nyJob = createJob("NYC Job", testCompany);
        nyJob.setLocation("New York, NY");
        
        Job sfJob = createJob("SF Job", testCompany);
        sfJob.setLocation("San Francisco, CA");
        
        jobRepository.saveAll(List.of(nyJob, sfJob));
        entityManager.flush();
        
        // Act
        Page<Job> result = jobRepository.searchJobs(
            null, "New York", null, null, null, null, null, null, true,
            PageRequest.of(0, 10)
        );
        
        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("NYC Job", result.getContent().get(0).getTitle());
    }
    
    @Test
    void searchJobs_WithSalaryRange_ReturnsMatchingJobs() {
        // Arrange
        Job highPayJob = createJob("High Pay Job", testCompany);
        highPayJob.setSalaryMin(new BigDecimal("100000"));
        highPayJob.setSalaryMax(new BigDecimal("150000"));
        
        Job lowPayJob = createJob("Low Pay Job", testCompany);
        lowPayJob.setSalaryMin(new BigDecimal("50000"));
        lowPayJob.setSalaryMax(new BigDecimal("70000"));
        
        jobRepository.saveAll(List.of(highPayJob, lowPayJob));
        entityManager.flush();
        
        // Act
        Page<Job> result = jobRepository.searchJobs(
            null, null, null, null, null, null,
            new BigDecimal("90000"), null, true,
            PageRequest.of(0, 10)
        );
        
        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("High Pay Job", result.getContent().get(0).getTitle());
    }
    
    @Test
    void searchJobs_WithMultipleFilters_ReturnsMatchingJobs() {
        // Arrange
        Job matchingJob = createJob("Senior Java Developer", testCompany);
        matchingJob.setLocation("New York, NY");
        matchingJob.setJobType(JobType.FULL_TIME);
        matchingJob.setExperienceLevel(ExperienceLevel.SENIOR);
        matchingJob.setRemoteOption(RemoteOption.REMOTE);
        matchingJob.setIsActive(true);
        
        Job nonMatchingJob = createJob("Junior Python Developer", testCompany);
        nonMatchingJob.setLocation("San Francisco, CA");
        nonMatchingJob.setJobType(JobType.PART_TIME);
        nonMatchingJob.setExperienceLevel(ExperienceLevel.ENTRY);
        
        jobRepository.saveAll(List.of(matchingJob, nonMatchingJob));
        entityManager.flush();
        
        // Act
        Page<Job> result = jobRepository.searchJobs(
            "java",
            "New York",
            testCompany.getId(),
            JobType.FULL_TIME,
            ExperienceLevel.SENIOR,
            RemoteOption.REMOTE,
            null,
            null,
            true,
            PageRequest.of(0, 10)
        );
        
        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Senior Java Developer", result.getContent().get(0).getTitle());
    }
    
    private Job createJob(String title, Company company) {
        Job job = new Job();
        job.setTitle(title);
        job.setDescription("Description for " + title);
        job.setCompany(company);
        job.setLocation("Default Location");
        job.setJobType(JobType.FULL_TIME);
        job.setExperienceLevel(ExperienceLevel.MID);
        job.setRemoteOption(RemoteOption.HYBRID);
        job.setIsActive(true);
        return job;
    }
}
```

**CompanyRepositoryTest.java** - Similar structure for company queries

#### 4. API Tests - Controller Layer

**JobControllerTest.java**:
```java
@WebMvcTest(JobController.class)
@Import(GlobalExceptionHandler.class)
class JobControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private JobService jobService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void getAllJobs_ReturnsListOfJobs() throws Exception {
        // Arrange
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(1L);
        jobDTO.setTitle("Java Developer");
        
        Page<JobDTO> page = new PageImpl<>(List.of(jobDTO));
        when(jobService.getAllJobs(any(Pageable.class))).thenReturn(page);
        
        // Act & Assert
        mockMvc.perform(get("/api/jobs")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].title").value("Java Developer"))
            .andExpect(jsonPath("$.totalElements").value(1));
    }
    
    @Test
    void getJobById_JobExists_ReturnsJob() throws Exception {
        // Arrange
        JobDetailDTO jobDTO = new JobDetailDTO();
        jobDTO.setId(1L);
        jobDTO.setTitle("Java Developer");
        jobDTO.setDescription("Build amazing things");
        
        when(jobService.getJobById(1L)).thenReturn(jobDTO);
        
        // Act & Assert
        mockMvc.perform(get("/api/jobs/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Java Developer"))
            .andExpect(jsonPath("$.description").value("Build amazing things"));
    }
    
    @Test
    void getJobById_JobNotFound_Returns404() throws Exception {
        // Arrange
        when(jobService.getJobById(999L)).thenThrow(new JobNotFoundException(999L));
        
        // Act & Assert
        mockMvc.perform(get("/api/jobs/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Job not found with id: 999"))
            .andExpect(jsonPath("$.status").value(404));
    }
    
    @Test
    void createJob_ValidData_ReturnsCreatedJob() throws Exception {
        // Arrange
        JobCreateDTO createDTO = new JobCreateDTO();
        createDTO.setTitle("Java Developer");
        createDTO.setDescription("Build amazing things");
        createDTO.setCompanyId(1L);
        createDTO.setLocation("New York");
        createDTO.setJobType(JobType.FULL_TIME);
        createDTO.setExperienceLevel(ExperienceLevel.SENIOR);
        createDTO.setRemoteOption(RemoteOption.REMOTE);
        
        JobDTO resultDTO = new JobDTO();
        resultDTO.setId(1L);
        resultDTO.setTitle("Java Developer");
        
        when(jobService.createJob(any(JobCreateDTO.class))).thenReturn(resultDTO);
        
        // Act & Assert
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Java Developer"));
    }
    
    @Test
    void createJob_InvalidData_Returns400() throws Exception {
        // Arrange
        JobCreateDTO createDTO = new JobCreateDTO();
        // Missing required fields
        
        // Act & Assert
        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.errors").isMap());
    }
    
    @Test
    void updateJob_ValidData_ReturnsUpdatedJob() throws Exception {
        // Arrange
        JobUpdateDTO updateDTO = new JobUpdateDTO();
        updateDTO.setTitle("Updated Title");
        
        JobDTO resultDTO = new JobDTO();
        resultDTO.setId(1L);
        resultDTO.setTitle("Updated Title");
        
        when(jobService.updateJob(eq(1L), any(JobUpdateDTO.class))).thenReturn(resultDTO);
        
        // Act & Assert
        mockMvc.perform(patch("/api/jobs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Title"));
    }
    
    @Test
    void deleteJob_JobExists_Returns204() throws Exception {
        // Arrange
        doNothing().when(jobService).deleteJob(1L);
        
        // Act & Assert
        mockMvc.perform(delete("/api/jobs/1"))
            .andExpect(status().isNoContent());
    }
    
    @Test
    void searchJobs_WithFilters_ReturnsFilteredJobs() throws Exception {
        // Arrange
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(1L);
        jobDTO.setTitle("Java Developer");
        
        Page<JobDTO> page = new PageImpl<>(List.of(jobDTO));
        when(jobService.searchJobs(
            anyString(), anyString(), anyLong(), any(), any(), any(),
            any(), any(), anyBoolean(), any(Pageable.class)
        )).thenReturn(page);
        
        // Act & Assert
        mockMvc.perform(get("/api/jobs/search")
                .param("keyword", "java")
                .param("location", "New York")
                .param("experienceLevel", "SENIOR"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title").value("Java Developer"));
    }
    
    @Test
    void getActiveJobs_ReturnsOnlyActiveJobs() throws Exception {
        // Arrange
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(1L);
        jobDTO.setIsActive(true);
        
        Page<JobDTO> page = new PageImpl<>(List.of(jobDTO));
        when(jobService.getActiveJobs(any(Pageable.class))).thenReturn(page);
        
        // Act & Assert
        mockMvc.perform(get("/api/jobs/active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].isActive").value(true));
    }
    
    @Test
    void deactivateJob_JobExists_ReturnsSuccessMessage() throws Exception {
        // Arrange
        doNothing().when(jobService).deactivateJob(1L);
        
        // Act & Assert
        mockMvc.perform(post("/api/jobs/1/deactivate"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Job successfully deactivated"))
            .andExpect(jsonPath("$.jobId").value(1));
    }
}
```

**CompanyControllerTest.java** - Similar structure for company endpoints

#### 5. Test Coverage Goals

- **Service Layer**: 90%+ coverage
- **Repository Layer**: 80%+ coverage (custom queries)
- **Controller Layer**: 85%+ coverage
- **Overall Project**: 80%+ coverage

#### Phase III Acceptance Criteria
- [ ] All service methods have unit tests
- [ ] All custom repository queries have integration tests
- [ ] All controller endpoints have API tests
- [ ] Tests cover happy path and error cases
- [ ] Validation errors properly tested
- [ ] Exception handling properly tested
- [ ] Concurrent update scenarios tested
- [ ] All tests pass consistently
- [ ] Test coverage meets goals
- [ ] Tests run in CI/CD pipeline (optional but recommended)

---

## Phase IV: Advanced Features

### Objectives
- Add authentication and authorization
- Implement file upload for resumes
- Create Application entity for job applications
- Add scheduled task to auto-expire old jobs

### Deliverables

#### 1. Authentication & Authorization

**Dependencies**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

**User Entity**:
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank
    @Column(nullable = false)
    private String password;  // BCrypt hashed
    
    @NotBlank
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;  // ADMIN, RECRUITER, JOB_SEEKER
    
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Getters, setters
}

public enum UserRole {
    ADMIN,
    RECRUITER,
    JOB_SEEKER
}
```

**Security Configuration**:
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/companies/**").permitAll()
                .requestMatchers("/api/jobs/**").hasAnyRole("ADMIN", "RECRUITER")
                .requestMatchers("/api/companies/**").hasAnyRole("ADMIN", "RECRUITER")
                .requestMatchers("/api/applications/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Auth Endpoints**:
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/refresh` - Refresh JWT token
- `GET /api/auth/me` - Get current user info

**Access Control**:
- Anonymous: Can view jobs and companies (GET only)
- JOB_SEEKER: Can view jobs, apply to jobs
- RECRUITER: Can create/edit/delete their company's jobs
- ADMIN: Can do everything

#### 2. File Upload (Resume Storage)

**Configuration**:
```java
@Configuration
public class FileStorageConfig {
    
    @Value("${file.upload-dir:./uploads/resumes}")
    private String uploadDir;
    
    @Bean
    public String uploadDirectory() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return uploadDir;
    }
}
```

**File Storage Service**:
```java
@Service
public class FileStorageService {
    
    private final String uploadDir;
    
    public String storeFile(MultipartFile file) {
        // Validate file type (pdf, doc, docx only)
        // Generate unique filename
        // Save to disk
        // Return file path or URL
    }
    
    public Resource loadFileAsResource(String filename) {
        // Load file from disk
        // Return as Resource for download
    }
    
    public void deleteFile(String filename) {
        // Delete file from disk
    }
}
```

**File Upload Endpoint**:
```java
@PostMapping("/upload-resume")
public ResponseEntity<FileUploadResponse> uploadResume(
    @RequestParam("file") MultipartFile file
) {
    // Validate file
    // Store file
    // Return file info
}
```

**Constraints**:
- Max file size: 5MB
- Allowed types: PDF, DOC, DOCX
- Filename sanitization
- Virus scanning (optional, can use ClamAV)

#### 3. Application Entity

**Application Entity**:
```java
@Entity
@Table(name = "application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "resume_path")
    private String resumePath;
    
    @Column(columnDefinition = "TEXT")
    private String coverLetter;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    @CreatedDate
    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;
    
    @Version
    private Integer version;
    
    // Prevent duplicate applications
    @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "user_id"}))
    
    // Getters, setters
}

public enum ApplicationStatus {
    PENDING,
    REVIEWING,
    INTERVIEWED,
    ACCEPTED,
    REJECTED
}
```

**Application Endpoints**:
- `POST /api/applications` - Apply to job (with resume upload)
- `GET /api/applications/my-applications` - Get user's applications
- `GET /api/applications/job/{jobId}` - Get all applications for a job (recruiter only)
- `PATCH /api/applications/{id}/status` - Update application status (recruiter only)
- `DELETE /api/applications/{id}` - Withdraw application

**Business Logic**:
- User can only apply once per job
- Must be authenticated
- Resume is optional if already uploaded
- Can't apply to expired/inactive jobs
- Recruiters can see applications for their company's jobs only

#### 4. Scheduled Task - Auto-Expire Jobs

**Configuration**:
```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
```

**Scheduled Task**:
```java
@Component
public class JobExpirationTask {
    
    private final JobRepository jobRepository;
    
    @Scheduled(cron = "0 0 2 * * ?")  // Run daily at 2 AM
    @Transactional
    public void expireOldJobs() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Job> expiredJobs = jobRepository.findAll().stream()
            .filter(job -> job.getIsActive() 
                && job.getExpiryDate() != null 
                && job.getExpiryDate().isBefore(now))
            .collect(Collectors.toList());
        
        expiredJobs.forEach(job -> job.setIsActive(false));
        jobRepository.saveAll(expiredJobs);
        
        log.info("Expired {} jobs", expiredJobs.size());
    }
}
```

**Alternative - Query-based**:
```java
@Query("UPDATE Job j SET j.isActive = false WHERE j.isActive = true " +
       "AND j.expiryDate IS NOT NULL AND j.expiryDate < CURRENT_TIMESTAMP")
@Modifying
int deactivateExpiredJobs();
```

#### Phase IV Acceptance Criteria
- [ ] Users can register and login
- [ ] JWT tokens properly issued and validated
- [ ] Role-based access control enforced
- [ ] Public endpoints accessible without auth
- [ ] Protected endpoints require valid token
- [ ] File upload works with validation
- [ ] Resume files stored securely
- [ ] File download works for authorized users
- [ ] Users can apply to jobs
- [ ] Duplicate applications prevented
- [ ] Recruiters can view applications for their jobs
- [ ] Application status can be updated
- [ ] Scheduled task runs and expires jobs
- [ ] Expired jobs automatically deactivated

---

## Non-Functional Requirements

### Performance
- API response time < 200ms for simple queries
- Search queries < 500ms
- Support 100+ concurrent users
- Database queries optimized with indexes
- Pagination prevents memory issues

### Security
- Passwords hashed with BCrypt
- JWT tokens with expiration
- SQL injection prevented (using JPA)
- Input validation on all endpoints
- CORS configured appropriately
- File upload validation

### Scalability
- Stateless architecture (JWT-based auth)
- Database connection pooling
- Prepared for horizontal scaling
- Async operations where appropriate

### Maintainability
- Clean code architecture (Controller → Service → Repository)
- Comprehensive test coverage
- Clear error messages
- Logging at appropriate levels
- DTOs separate from entities
- Environment-specific configuration

### Reliability
- Optimistic locking prevents data corruption
- Transaction management for data consistency
- Exception handling at all layers
- Database constraints enforce referential integrity
- Graceful degradation on errors

---

## Development Guidelines

### Code Style
- Follow Java naming conventions
- Use Lombok to reduce boilerplate
- Keep methods small and focused
- Use meaningful variable names
- Add JavaDoc for public methods

### Git Workflow
- Feature branches: `feature/phase-1-crud`
- Commit messages: `feat: Add job search endpoint`
- PR for each phase
- Squash commits before merge

### Documentation
- README with setup instructions
- API documentation (Swagger/OpenAPI optional)
- Database schema diagram
- Environment variable documentation

---

## Deployment Considerations

### Docker Setup
```dockerfile
# Dockerfile for Spring Boot app
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose for full stack:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: job_board
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/job_board
      SPRING_DATASOURCE_USERNAME: admin
      SPRING_DATASOURCE_PASSWORD: admin123
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### Environment Variables
```
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/job_board
DATABASE_USERNAME=admin
DATABASE_PASSWORD=admin123

# JWT
JWT_SECRET=your-secret-key-min-256-bits
JWT_EXPIRATION=86400000

# File Upload
FILE_UPLOAD_DIR=./uploads/resumes
MAX_FILE_SIZE=5MB

# Server
SERVER_PORT=8080
```

---

## Success Criteria

### Phase I
- ✅ Docker Compose setup works
- ✅ All basic CRUD endpoints functional
- ✅ Validation and error handling in place
- ✅ Concurrency properly handled

### Phase II
- ✅ Search with multiple filters works
- ✅ Pagination implemented
- ✅ Active jobs endpoint works
- ✅ Deactivate functionality works

### Phase III
- ✅ 80%+ test coverage achieved
- ✅ All critical paths tested
- ✅ Tests pass consistently
- ✅ CI/CD pipeline configured (optional)

### Phase IV
- ✅ Authentication system functional
- ✅ File upload/download works
- ✅ Application system complete
- ✅ Scheduled task runs correctly

---

## Timeline Estimate

- **Phase I**: 3-4 days (setup + basic CRUD)
- **Phase II**: 2-3 days (advanced routes)
- **Phase III**: 2-3 days (testing)
- **Phase IV**: 4-5 days (advanced features)

**Total**: 11-15 days for full completion

---

## Additional Resources

### Documentation
- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)

### Tools
- Postman/Insomnia for API testing
- pgAdmin for database management
- IntelliJ IDEA / VS Code for development

### Learning Resources
- Baeldung Spring Boot tutorials
- Spring official guides
- Java Brains YouTube channel

---

## Notes

- This PRD is comprehensive but flexible - adjust as needed based on learning and discoveries
- Focus on one phase at a time - don't skip ahead
- Test incrementally as you build
- Ask questions when stuck - don't waste time on blockers
- Document learnings for interview prep
- Keep code clean and well-organized from the start

Good luck! 🚀
