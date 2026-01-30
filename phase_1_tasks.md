# Phase I: Core Setup & Basic CRUD - Implementation Tasks

**Reference**: See PRD Section "Phase I: Core Setup & Basic CRUD"

**Overview**: Set up Spring Boot project with PostgreSQL database, implement basic CRUD operations for Jobs and Companies, add validation and error handling, implement concurrency control with optimistic locking.

---

## PR #1: Project Setup & Configuration

**PRD Reference**: Phase I, Deliverable 1 - Project Setup

### Tasks

- [x] 1. Initialize Spring Boot project using Spring Initializr
  - [x] Select Java 17
  - [x] Select Maven as build tool
  - [x] Select Spring Boot 3.2.x (latest stable)
  - [x] Add dependencies: Spring Web, Spring Data JPA, PostgreSQL Driver, Validation, Lombok
  - [x] Set Group: `com.jobboard`
  - [x] Set Artifact: `job-board-api`

- [x] 2. Create project structure
  ```
  src/main/java/com/jobboard/
    ├── JobBoardApiApplication.java
    ├── config/
    ├── controller/
    ├── service/
    ├── repository/
    ├── model/
    │   ├── entity/
    │   └── dto/
    ├── exception/
    └── util/
  ```

- [x] 3. Set up Docker Compose for PostgreSQL
  - [x] Create `docker-compose.yml` in project root
  - [x] Configure PostgreSQL service (image: postgres:15-alpine)
  - [x] Set environment variables (POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD)
  - [x] Configure port mapping (5432:5432)
  - [x] Add volume for data persistence
  - [x] Test: Run `docker-compose up -d` and verify PostgreSQL is running

- [x] 4. Configure application properties
  - [x] Create `src/main/resources/application.yml`
  - [x] Add datasource configuration (URL, username, password, driver)
  - [x] Add JPA/Hibernate configuration (ddl-auto, show-sql, format-sql, dialect)
  - [x] Add Jackson date serialization config
  - [x] Add server port configuration (8080)
  - [x] Add logging configuration for SQL and Hibernate

- [x] 5. Verify application starts successfully
  - [x] Start PostgreSQL: `docker-compose up -d`
  - [x] Run Spring Boot application
  - [x] Check logs for successful database connection
  - [x] Verify no errors on startup

- [x] 6. Create `.gitignore` file
  - [x] Add Maven build directories (target/)
  - [x] Add IDE files (.idea/, *.iml, .vscode/)
  - [x] Add OS files (.DS_Store, Thumbs.db)
  - [x] Add environment files (*.env)
  - [x] Add log files (*.log)

- [x] 7. Initialize Git repository
  - [x] Run `git init`
  - [x] Add all files: `git add .`
  - [x] Initial commit: `git commit -m "feat: Initial project setup with Spring Boot and PostgreSQL"`

**Acceptance Criteria**:
- PostgreSQL container starts successfully
- Spring Boot application connects to database
- No errors in application logs
- Project structure follows best practices

---

## PR #2: Company Entity & Repository

**PRD Reference**: Phase I, Database Schema - Company Table

### Tasks

- [x] 1. Create Company entity enums (if needed)
  - [x] None required for Company in Phase I

- [x] 2. Create Company entity class
  - [x] Create `model/entity/Company.java`
  - [x] Add `@Entity` and `@Table(name = "company")` annotations
  - [x] Add id field with `@Id` and `@GeneratedValue(strategy = IDENTITY)`
  - [x] Add name field with `@NotBlank` and `@Column(nullable = false)`
  - [x] Add description field with `@Column(columnDefinition = "TEXT")`
  - [x] Add website field with `@Pattern` validation for URL format
  - [x] Add location field with `@NotBlank`
  - [x] Add jobs relationship with `@OneToMany(mappedBy = "company", cascade = ALL, orphanRemoval = true)`
  - [x] Add createdAt field with `@CreatedDate` and `@Column(updatable = false)`
  - [x] Add updatedAt field with `@LastModifiedDate`
  - [x] Add version field with `@Version` for optimistic locking
  - [x] Generate getters, setters, constructors using Lombok `@Data`

- [x] 3. Enable JPA Auditing for timestamps
  - [x] Create `config/JpaConfig.java`
  - [x] Add `@Configuration` annotation
  - [x] Add `@EnableJpaAuditing` annotation

- [x] 4. Create CompanyRepository interface
  - [x] Create `repository/CompanyRepository.java`
  - [x] Extend `JpaRepository<Company, Long>`
  - [x] Add method: `Optional<Company> findByName(String name)`

**Acceptance Criteria**:
- Company entity created with all required fields
- Repository interface created
- Database table auto-generated correctly
- All constraints and indexes in place

---

## PR #3: Job Entity & Repository with Enums

**PRD Reference**: Phase I, Database Schema - Job Table & Enums

### Tasks

- [x] 1. Create Job-related enums
  - [x] Create `model/entity/JobType.java` enum (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP)
  - [x] Create `model/entity/ExperienceLevel.java` enum (ENTRY, MID, SENIOR)
  - [x] Create `model/entity/RemoteOption.java` enum (REMOTE, HYBRID, ONSITE)

- [x] 2. Create Job entity class
  - [x] Create `model/entity/Job.java`
  - [x] Add `@Entity` and `@Table(name = "job")` annotations
  - [x] Add id field with `@Id` and `@GeneratedValue`
  - [x] Add title field with `@NotBlank` and `@Column(nullable = false)`
  - [x] Add description field with `@NotBlank` and `@Column(columnDefinition = "TEXT")`
  - [x] Add company relationship with `@ManyToOne(fetch = LAZY)` and `@JoinColumn(name = "company_id")`
  - [x] Add location field with `@NotBlank`
  - [x] Add salaryMin field with `@DecimalMin("0.0")` and `@Column(precision = 10, scale = 2)`
  - [x] Add salaryMax field with `@DecimalMin("0.0")` and `@Column(precision = 10, scale = 2)`
  - [x] Add jobType field with `@NotNull` and `@Enumerated(STRING)`
  - [x] Add experienceLevel field with `@NotNull` and `@Enumerated(STRING)`
  - [x] Add remoteOption field with `@NotNull` and `@Enumerated(STRING)`
  - [x] Add postedDate field with `@Column(nullable = false)`
  - [x] Add expiryDate field with `@Future` validation
  - [x] Add isActive field with default value `true`
  - [x] Add applicationUrl field with `@Pattern` validation for URL
  - [x] Add createdAt, updatedAt with JPA auditing annotations
  - [x] Add version field with `@Version`
  - [x] Add `@PrePersist` method to set postedDate to now

- [x] 3. Add indexes to Job entity
  - [x] Add `@Table` annotation with indexes array
  - [x] Create index on company_id
  - [x] Create index on is_active
  - [x] Create index on posted_date

- [x] 4. Create JobRepository interface
  - [x] Create `repository/JobRepository.java`
  - [x] Extend `JpaRepository<Job, Long>`
  - [x] Add method: `List<Job> findByCompanyId(Long companyId)`

- [x] 5. Test database schema generation
  - [x] Start application
  - [x] Verify `job` table exists with all columns
  - [x] Verify foreign key constraint to company table
  - [x] Verify ON DELETE CASCADE is configured
  - [x] Verify all indexes created
  - [x] Verify enum columns are VARCHAR type

**Acceptance Criteria**:
- All three enums created
- Job entity created with all fields and validation
- Foreign key relationship to Company configured
- Indexes created on appropriate columns
- Repository interface created

---

## PR #4: DTOs for Company

**PRD Reference**: Phase I, Deliverable 4 - DTOs

### Tasks

- [ ] 1. Create CompanySummaryDTO (for embedded responses)
  - [ ] Create `model/dto/CompanySummaryDTO.java`
  - [ ] Add fields: id, name, location
  - [ ] Use Lombok `@Data` for getters/setters
  - [ ] Add no-args and all-args constructors

- [ ] 2. Create CompanyDTO (for list responses)
  - [ ] Create `model/dto/CompanyDTO.java`
  - [ ] Add fields: id, name, description, website, location, createdAt, updatedAt
  - [ ] Use Lombok `@Data`
  - [ ] Add constructors

- [ ] 3. Create CompanyCreateDTO (for POST requests)
  - [ ] Create `model/dto/CompanyCreateDTO.java`
  - [ ] Add fields: name, description, website, location
  - [ ] Add validation annotations matching entity
  - [ ] Use Lombok `@Data`

- [ ] 4. Create CompanyUpdateDTO (for PATCH requests)
  - [ ] Create `model/dto/CompanyUpdateDTO.java`
  - [ ] Add optional fields: name, description, website, location
  - [ ] All fields nullable for partial updates
  - [ ] Add validation annotations (only validate if present)
  - [ ] Use Lombok `@Data`

- [ ] 5. Create DTO mapper utility for Company
  - [ ] Create `util/CompanyMapper.java` class
  - [ ] Add method: `CompanyDTO toDTO(Company entity)`
  - [ ] Add method: `CompanySummaryDTO toSummaryDTO(Company entity)`
  - [ ] Add method: `Company toEntity(CompanyCreateDTO dto)`
  - [ ] Add method: `void updateEntityFromDTO(Company entity, CompanyUpdateDTO dto)`
  - [ ] Handle null checks in mapper methods

**Acceptance Criteria**:
- All Company DTOs created
- Validation annotations applied correctly
- Mapper utility created with conversion methods
- DTOs properly separate concerns from entities

---

## PR #5: DTOs for Job

**PRD Reference**: Phase I, Deliverable 4 - DTOs

### Tasks

- [ ] 1. Create JobDTO (for list responses)
  - [ ] Create `model/dto/JobDTO.java`
  - [ ] Add fields: id, title, location, salaryMin, salaryMax, jobType, experienceLevel, remoteOption, postedDate, isActive
  - [ ] Add field: CompanySummaryDTO company (embedded company info)
  - [ ] Use Lombok `@Data`
  - [ ] Add constructors

- [ ] 2. Create JobDetailDTO (for single job responses)
  - [ ] Create `model/dto/JobDetailDTO.java`
  - [ ] Extend JobDTO or include all JobDTO fields
  - [ ] Add additional fields: description, expiryDate, applicationUrl, createdAt, updatedAt
  - [ ] Use Lombok `@Data`

- [ ] 3. Create JobCreateDTO (for POST requests)
  - [ ] Create `model/dto/JobCreateDTO.java`
  - [ ] Add fields: title, description, companyId, location, salaryMin, salaryMax, jobType, experienceLevel, remoteOption, expiryDate, applicationUrl
  - [ ] Add validation annotations matching entity
  - [ ] Use `@NotBlank`, `@NotNull`, `@DecimalMin`, `@Future`, `@Pattern` as appropriate
  - [ ] Use Lombok `@Data`

- [ ] 4. Create JobUpdateDTO (for PATCH requests)
  - [ ] Create `model/dto/JobUpdateDTO.java`
  - [ ] Add optional fields matching JobCreateDTO
  - [ ] All fields nullable for partial updates
  - [ ] Add validation annotations (only validate if present)
  - [ ] Use Lombok `@Data`

- [ ] 5. Create custom validator for salary range
  - [ ] Create `validation/ValidSalaryRange.java` annotation
  - [ ] Create `validation/SalaryRangeValidator.java` class
  - [ ] Implement logic: salaryMax >= salaryMin if both present
  - [ ] Apply annotation to JobCreateDTO and JobUpdateDTO

- [ ] 6. Create DTO mapper utility for Job
  - [ ] Create `util/JobMapper.java` class
  - [ ] Add method: `JobDTO toDTO(Job entity)`
  - [ ] Add method: `JobDetailDTO toDetailDTO(Job entity)`
  - [ ] Add method: `Job toEntity(JobCreateDTO dto, Company company)`
  - [ ] Add method: `void updateEntityFromDTO(Job entity, JobUpdateDTO dto, Company company)`
  - [ ] Include CompanyMapper for embedded company data
  - [ ] Handle null checks and optional fields

**Acceptance Criteria**:
- All Job DTOs created
- Custom salary range validator implemented
- Mapper utility created with all conversion methods
- JobDetailDTO extends or properly includes JobDTO fields

---

## PR #6: Custom Exceptions

**PRD Reference**: Phase I, Deliverable 8 - Exception Handling (Custom Exceptions)

### Tasks

- [ ] 1. Create JobNotFoundException
  - [ ] Create `exception/JobNotFoundException.java`
  - [ ] Extend `RuntimeException`
  - [ ] Add constructor accepting Long id
  - [ ] Set message: "Job not found with id: {id}"

- [ ] 2. Create CompanyNotFoundException
  - [ ] Create `exception/CompanyNotFoundException.java`
  - [ ] Extend `RuntimeException`
  - [ ] Add constructor accepting Long id
  - [ ] Set message: "Company not found with id: {id}"

- [ ] 3. Create InvalidJobDataException
  - [ ] Create `exception/InvalidJobDataException.java`
  - [ ] Extend `RuntimeException`
  - [ ] Add constructor accepting String message
  - [ ] Use for business logic validation failures

- [ ] 4. Create OptimisticLockException (optional, Spring has built-in)
  - [ ] Create `exception/OptimisticLockException.java`
  - [ ] Extend `RuntimeException`
  - [ ] Add constructor accepting String message
  - [ ] Use as wrapper for Spring's ObjectOptimisticLockingFailureException if needed

**Acceptance Criteria**:
- All custom exception classes created
- Exceptions extend RuntimeException (unchecked)
- Clear, descriptive error messages
- Ready to be thrown from service layer

---

## PR #7: Error Response DTOs

**PRD Reference**: Phase I, Deliverable 8 - Exception Handling (Error Response DTOs)

### Tasks

- [ ] 1. Create ErrorResponse DTO
  - [ ] Create `model/dto/ErrorResponse.java`
  - [ ] Add fields: message (String), status (int), timestamp (LocalDateTime)
  - [ ] Add all-args constructor
  - [ ] Add getters (use Lombok `@Getter` and `@AllArgsConstructor`)

- [ ] 2. Create ValidationErrorResponse DTO
  - [ ] Create `model/dto/ValidationErrorResponse.java`
  - [ ] Extend ErrorResponse or include its fields
  - [ ] Add field: errors (Map<String, String>) for field-level errors
  - [ ] Add all-args constructor
  - [ ] Add getters

**Acceptance Criteria**:
- ErrorResponse DTO created for general errors
- ValidationErrorResponse created for validation errors
- Both DTOs properly structured for JSON serialization
- Ready to be used in exception handler

---

## PR #8: Global Exception Handler

**PRD Reference**: Phase I, Deliverable 8 - Exception Handling (Global Exception Handler)

### Tasks

- [ ] 1. Create GlobalExceptionHandler class
  - [ ] Create `exception/GlobalExceptionHandler.java`
  - [ ] Add `@RestControllerAdvice` annotation

- [ ] 2. Handle JobNotFoundException
  - [ ] Add method with `@ExceptionHandler(JobNotFoundException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.NOT_FOUND)`
  - [ ] Return ErrorResponse with message, 404 status, and current timestamp

- [ ] 3. Handle CompanyNotFoundException
  - [ ] Add method with `@ExceptionHandler(CompanyNotFoundException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.NOT_FOUND)`
  - [ ] Return ErrorResponse with message, 404 status, and timestamp

- [ ] 4. Handle MethodArgumentNotValidException (validation errors)
  - [ ] Add method with `@ExceptionHandler(MethodArgumentNotValidException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.BAD_REQUEST)`
  - [ ] Extract field errors from BindingResult
  - [ ] Build Map<String, String> of field names to error messages
  - [ ] Return ValidationErrorResponse with errors map, 400 status, and timestamp

- [ ] 5. Handle ObjectOptimisticLockingFailureException (concurrency)
  - [ ] Add method with `@ExceptionHandler(ObjectOptimisticLockingFailureException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.CONFLICT)`
  - [ ] Return ErrorResponse with user-friendly message about concurrent modification
  - [ ] Use 409 status and timestamp

- [ ] 6. Handle generic Exception (catch-all)
  - [ ] Add method with `@ExceptionHandler(Exception.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)`
  - [ ] Return ErrorResponse with generic message "An unexpected error occurred"
  - [ ] Use 500 status and timestamp
  - [ ] Log full exception with logger for debugging

- [ ] 7. Add SLF4J logger
  - [ ] Add `private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class)`
  - [ ] Log error details in catch-all handler

**Acceptance Criteria**:
- Global exception handler created with @RestControllerAdvice
- All exception types handled with appropriate HTTP status codes
- Error responses properly formatted
- Validation errors return field-specific messages
- Optimistic locking failures return 409 Conflict
- Generic errors logged for debugging

---

## PR #9: Company Service Layer

**PRD Reference**: Phase I, Deliverable 6 - Service Layer

### Tasks

- [ ] 1. Create CompanyService class
  - [ ] Create `service/CompanyService.java`
  - [ ] Add `@Service` annotation
  - [ ] Add `@Transactional` annotation at class level
  - [ ] Inject CompanyRepository via constructor
  - [ ] Inject CompanyMapper via field or constructor

- [ ] 2. Implement getAllCompanies method
  - [ ] Add `@Transactional(readOnly = true)` annotation
  - [ ] Method signature: `List<CompanyDTO> getAllCompanies()`
  - [ ] Fetch all companies from repository
  - [ ] Convert entities to DTOs using mapper
  - [ ] Return list of CompanyDTOs

- [ ] 3. Implement getCompanyById method
  - [ ] Add `@Transactional(readOnly = true)` annotation
  - [ ] Method signature: `CompanyDTO getCompanyById(Long id)`
  - [ ] Fetch company by id using repository.findById()
  - [ ] Throw CompanyNotFoundException if not found
  - [ ] Convert entity to DTO
  - [ ] Return CompanyDTO

- [ ] 4. Implement createCompany method
  - [ ] Method signature: `CompanyDTO createCompany(CompanyCreateDTO dto)`
  - [ ] Convert DTO to entity using mapper
  - [ ] Save entity using repository
  - [ ] Convert saved entity back to DTO
  - [ ] Return CompanyDTO

- [ ] 5. Implement updateCompany method
  - [ ] Method signature: `CompanyDTO updateCompany(Long id, CompanyUpdateDTO dto)`
  - [ ] Fetch existing company by id (throw exception if not found)
  - [ ] Update entity fields from DTO using mapper (only update non-null fields)
  - [ ] Save updated entity
  - [ ] Convert to DTO
  - [ ] Return CompanyDTO

- [ ] 6. Implement deleteCompany method
  - [ ] Method signature: `void deleteCompany(Long id)`
  - [ ] Fetch company by id (throw exception if not found)
  - [ ] Delete using repository.delete()
  - [ ] Note: This will cascade delete all associated jobs

**Acceptance Criteria**:
- CompanyService created with all CRUD methods
- Proper transaction management with @Transactional
- Read-only transactions for query methods
- Exceptions thrown when entities not found
- DTOs used for all inputs and outputs

---

## PR #10: Job Service Layer

**PRD Reference**: Phase I, Deliverable 6 - Service Layer

### Tasks

- [ ] 1. Create JobService class
  - [ ] Create `service/JobService.java`
  - [ ] Add `@Service` annotation
  - [ ] Add `@Transactional` annotation at class level
  - [ ] Inject JobRepository via constructor
  - [ ] Inject CompanyRepository via constructor
  - [ ] Inject JobMapper via field or constructor

- [ ] 2. Implement getAllJobs method
  - [ ] Add `@Transactional(readOnly = true)` annotation
  - [ ] Method signature: `List<JobDTO> getAllJobs()`
  - [ ] Use JPQL query with JOIN FETCH to load company data (avoid N+1)
  - [ ] Or use repository.findAll() and mapper will fetch company
  - [ ] Convert entities to DTOs using mapper
  - [ ] Return list of JobDTOs with embedded company data

- [ ] 3. Implement getJobById method
  - [ ] Add `@Transactional(readOnly = true)` annotation
  - [ ] Method signature: `JobDetailDTO getJobById(Long id)`
  - [ ] Fetch job by id using repository.findById()
  - [ ] Throw JobNotFoundException if not found
  - [ ] Convert entity to JobDetailDTO (includes all fields)
  - [ ] Return JobDetailDTO

- [ ] 4. Implement createJob method
  - [ ] Method signature: `JobDTO createJob(JobCreateDTO dto)`
  - [ ] Validate company exists: fetch by companyId
  - [ ] Throw CompanyNotFoundException if company not found
  - [ ] Convert DTO to entity using mapper (pass company object)
  - [ ] Save entity using repository
  - [ ] Convert saved entity back to DTO
  - [ ] Return JobDTO

- [ ] 5. Implement updateJob method
  - [ ] Method signature: `JobDTO updateJob(Long id, JobUpdateDTO dto)`
  - [ ] Fetch existing job by id (throw exception if not found)
  - [ ] If companyId in DTO, validate new company exists
  - [ ] Update entity fields from DTO using mapper (only non-null fields)
  - [ ] If company changed, update the relationship
  - [ ] Save updated entity (optimistic lock version will be checked)
  - [ ] Convert to DTO
  - [ ] Return JobDTO

- [ ] 6. Implement deleteJob method
  - [ ] Method signature: `void deleteJob(Long id)`
  - [ ] Fetch job by id (throw exception if not found)
  - [ ] Delete using repository.delete()

- [ ] 7. Add helper method to avoid N+1 queries
  - [ ] Consider adding custom repository method with JOIN FETCH
  - [ ] Or ensure mapper handles lazy loading properly
  - [ ] Test with SQL logging to verify only necessary queries

**Acceptance Criteria**:
- JobService created with all CRUD methods
- Company validation before creating/updating jobs
- Proper transaction management
- N+1 query problem avoided for company data
- Optimistic locking automatically handled by @Version
- Exceptions thrown appropriately

---

## PR #11: Company Controller

**PRD Reference**: Phase I, Deliverable 7 - Controller Layer & Deliverable 2 - API Endpoints (Company)

### Tasks

- [ ] 1. Create CompanyController class
  - [ ] Create `controller/CompanyController.java`
  - [ ] Add `@RestController` annotation
  - [ ] Add `@RequestMapping("/api/companies")` annotation
  - [ ] Add `@Validated` annotation for method-level validation
  - [ ] Inject CompanyService via constructor

- [ ] 2. Implement GET /api/companies (get all)
  - [ ] Add method with `@GetMapping` annotation
  - [ ] Method signature: `ResponseEntity<List<CompanyDTO>> getAllCompanies()`
  - [ ] Call service.getAllCompanies()
  - [ ] Return ResponseEntity.ok() with list

- [ ] 3. Implement GET /api/companies/{id} (get by id)
  - [ ] Add method with `@GetMapping("/{id}")` annotation
  - [ ] Method signature: `ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id)`
  - [ ] Call service.getCompanyById(id)
  - [ ] Return ResponseEntity.ok() with DTO

- [ ] 4. Implement POST /api/companies (create)
  - [ ] Add method with `@PostMapping` annotation
  - [ ] Method signature: `ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyCreateDTO dto)`
  - [ ] Call service.createCompany(dto)
  - [ ] Return ResponseEntity.status(HttpStatus.CREATED).body(result)

- [ ] 5. Implement PATCH /api/companies/{id} (update)
  - [ ] Add method with `@PatchMapping("/{id}")` annotation
  - [ ] Method signature: `ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyUpdateDTO dto)`
  - [ ] Call service.updateCompany(id, dto)
  - [ ] Return ResponseEntity.ok() with updated DTO

- [ ] 6. Implement DELETE /api/companies/{id} (delete)
  - [ ] Add method with `@DeleteMapping("/{id}")` annotation
  - [ ] Method signature: `ResponseEntity<Void> deleteCompany(@PathVariable Long id)`
  - [ ] Call service.deleteCompany(id)
  - [ ] Return ResponseEntity.noContent().build()

**Acceptance Criteria**:
- CompanyController created with all endpoints
- Proper HTTP methods used (GET, POST, PATCH, DELETE)
- Validation enabled with @Valid
- Correct HTTP status codes returned (200, 201, 204)
- PathVariable and RequestBody used correctly

---

## PR #12: Job Controller

**PRD Reference**: Phase I, Deliverable 7 - Controller Layer & Deliverable 3 - API Endpoints (Job)

### Tasks

- [ ] 1. Create JobController class
  - [ ] Create `controller/JobController.java`
  - [ ] Add `@RestController` annotation
  - [ ] Add `@RequestMapping("/api/jobs")` annotation
  - [ ] Add `@Validated` annotation
  - [ ] Inject JobService via constructor

- [ ] 2. Implement GET /api/jobs (get all)
  - [ ] Add method with `@GetMapping` annotation
  - [ ] Method signature: `ResponseEntity<List<JobDTO>> getAllJobs()`
  - [ ] Call service.getAllJobs()
  - [ ] Return ResponseEntity.ok() with list (includes embedded company data)

- [ ] 3. Implement GET /api/jobs/{id} (get by id with full details)
  - [ ] Add method with `@GetMapping("/{id}")` annotation
  - [ ] Method signature: `ResponseEntity<JobDetailDTO> getJobById(@PathVariable Long id)`
  - [ ] Call service.getJobById(id)
  - [ ] Return ResponseEntity.ok() with JobDetailDTO

- [ ] 4. Implement POST /api/jobs (create)
  - [ ] Add method with `@PostMapping` annotation
  - [ ] Method signature: `ResponseEntity<JobDTO> createJob(@Valid @RequestBody JobCreateDTO dto)`
  - [ ] Call service.createJob(dto)
  - [ ] Return ResponseEntity.status(HttpStatus.CREATED).body(result)

- [ ] 5. Implement PATCH /api/jobs/{id} (update)
  - [ ] Add method with `@PatchMapping("/{id}")` annotation
  - [ ] Method signature: `ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @Valid @RequestBody JobUpdateDTO dto)`
  - [ ] Call service.updateJob(id, dto)
  - [ ] Return ResponseEntity.ok() with updated DTO

- [ ] 6. Implement DELETE /api/jobs/{id} (delete)
  - [ ] Add method with `@DeleteMapping("/{id}")` annotation
  - [ ] Method signature: `ResponseEntity<Void> deleteJob(@PathVariable Long id)`
  - [ ] Call service.deleteJob(id)
  - [ ] Return ResponseEntity.noContent().build()

**Acceptance Criteria**:
- JobController created with all endpoints
- GET /api/jobs returns jobs with company data via join
- GET /api/jobs/{id} returns full details (JobDetailDTO)
- All endpoints properly validated
- Correct HTTP status codes used
- Exception handling works (tested manually or in next phase)

---

## Phase I Manual Testing Checklist

**Note**: Execute these tests using Postman, Insomnia, or curl

### Setup
- [ ] PostgreSQL container is running
- [ ] Application starts without errors
- [ ] Database tables created (company, job)

### Company Endpoints

#### Create Company (POST /api/companies)
- [ ] Create company with all fields - returns 201
- [ ] Create company without name - returns 400 with validation error
- [ ] Create company with invalid website URL - returns 400

#### Get All Companies (GET /api/companies)
- [ ] Returns empty list initially - returns 200
- [ ] After creating companies, returns full list - returns 200

#### Get Company By ID (GET /api/companies/{id})
- [ ] Get existing company - returns 200 with company data
- [ ] Get non-existent company - returns 404 with error message

#### Update Company (PATCH /api/companies/{id})
- [ ] Update single field (e.g., name only) - returns 200
- [ ] Update multiple fields - returns 200
- [ ] Verify unchanged fields remain the same
- [ ] Update non-existent company - returns 404

#### Delete Company (DELETE /api/companies/{id})
- [ ] Delete existing company - returns 204
- [ ] Verify company no longer exists - GET returns 404
- [ ] Delete non-existent company - returns 404

### Job Endpoints

#### Create Job (POST /api/jobs)
- [ ] Create job with valid company ID - returns 201
- [ ] Create job with invalid company ID - returns 404
- [ ] Create job without required fields - returns 400
- [ ] Create job with invalid enum values - returns 400
- [ ] Create job with salaryMax < salaryMin - returns 400
- [ ] Verify postedDate is auto-set

#### Get All Jobs (GET /api/jobs)
- [ ] Returns empty list initially - returns 200
- [ ] After creating jobs, returns list with embedded company data
- [ ] Verify company name and location are included

#### Get Job By ID (GET /api/jobs/{id})
- [ ] Get existing job - returns 200 with full details including description
- [ ] Get non-existent job - returns 404

#### Update Job (PATCH /api/jobs/{id})
- [ ] Update single field - returns 200
- [ ] Update company ID to different valid company - returns 200
- [ ] Update with invalid company ID - returns 404
- [ ] Verify optimistic locking version increments

#### Delete Job (DELETE /api/jobs/{id})
- [ ] Delete existing job - returns 204
- [ ] Verify job deleted - GET returns 404
- [ ] Delete non-existent job - returns 404

### Concurrency Testing

#### Optimistic Locking
- [ ] Fetch same job in two separate requests (note version number)
- [ ] Update job in first request - succeeds
- [ ] Attempt to update same job with old version in second request - returns 409 Conflict
- [ ] Verify error message mentions concurrent modification

### Relationship Testing

#### Cascade Delete
- [ ] Create company with multiple jobs
- [ ] Delete the company
- [ ] Verify all associated jobs are also deleted (ON DELETE CASCADE)

### Validation Testing
- [ ] Test @NotBlank on required string fields
- [ ] Test @NotNull on required fields
- [ ] Test @DecimalMin on salary fields
- [ ] Test @Future on expiryDate
- [ ] Test @Pattern on URL fields
- [ ] Test enum validation

### Error Response Format
- [ ] Verify 404 errors return ErrorResponse with message, status, timestamp
- [ ] Verify 400 validation errors return ValidationErrorResponse with field errors
- [ ] Verify 409 optimistic lock errors return proper message
- [ ] Verify 500 errors return generic message (trigger by causing unexpected error)

---

## Phase I Completion Checklist

- [ ] All PRs merged and code reviewed
- [ ] All manual tests passing
- [ ] Database schema matches PRD specification
- [ ] Foreign key relationships working correctly
- [ ] Optimistic locking functioning as expected
- [ ] Validation working on all endpoints
- [ ] Exception handling working for all error scenarios
- [ ] No N+1 query issues (check SQL logs)
- [ ] Timestamps auto-populating correctly
- [ ] Code follows clean architecture (Controller → Service → Repository)
- [ ] Git history is clean with meaningful commit messages
- [ ] README updated with setup instructions (optional for Phase I)

**When all items checked**: Phase I is complete! Proceed to Phase II.
