# Phase IV Part 2: File Upload, Applications & Scheduled Tasks - Implementation Tasks

**Reference**: See PRD Section "Phase IV: Advanced Features" - Deliverables 2, 3, 4

**Overview**: Implement file upload for resumes, create Application entity for job applications, and add scheduled task to auto-expire jobs.

---

## PR #41: File Storage Configuration

**PRD Reference**: Phase IV, Deliverable 2 - File Upload (Configuration)

### Tasks

- [ ] 1. Add file upload properties to application.yml
  - [ ] Add `file.upload-dir` property (default: ./uploads/resumes)
  - [ ] Add `file.max-size` property (5MB in bytes: 5242880)
  - [ ] Add `file.allowed-extensions` list (pdf, doc, docx)

- [ ] 2. Configure multipart file upload in Spring Boot
  - [ ] Add spring.servlet.multipart.max-file-size: 5MB
  - [ ] Add spring.servlet.multipart.max-request-size: 5MB
  - [ ] These prevent large file uploads

- [ ] 3. Create FileStorageConfig class
  - [ ] Create `config/FileStorageConfig.java`
  - [ ] Add `@Configuration` annotation
  - [ ] Inject file.upload-dir using `@Value`
  - [ ] Create `@Bean` method for uploadDirectory
  - [ ] Create directory if doesn't exist using `Files.createDirectories()`
  - [ ] Return absolute path as String

- [ ] 4. Create uploads directory
  - [ ] Ensure directory exists at configured path
  - [ ] Add to .gitignore to avoid committing uploaded files
  - [ ] Test write permissions

**Acceptance Criteria**:
- File upload properties configured
- FileStorageConfig created
- Upload directory auto-created on startup
- Max file size enforced

---

## PR #42: File Storage Service

**PRD Reference**: Phase IV, Deliverable 2 - File Upload (File Storage Service)

### Tasks

- [ ] 1. Create FileStorageService class
  - [ ] Create `service/FileStorageService.java`
  - [ ] Add `@Service` annotation
  - [ ] Inject uploadDirectory from config
  - [ ] Inject allowed extensions from config

- [ ] 2. Implement storeFile method
  - [ ] Method signature: `String storeFile(MultipartFile file)`
  - [ ] Validate file is not empty
  - [ ] Validate file extension is allowed (pdf, doc, docx)
  - [ ] Generate unique filename (UUID + original extension)
  - [ ] Sanitize filename to prevent path traversal attacks
  - [ ] Get target Path using uploadDirectory + filename
  - [ ] Copy file to target location using `Files.copy()`
  - [ ] Return filename (or relative path)

- [ ] 3. Implement loadFileAsResource method
  - [ ] Method signature: `Resource loadFileAsResource(String filename)`
  - [ ] Sanitize filename
  - [ ] Build file Path from uploadDirectory + filename
  - [ ] Check file exists and is readable
  - [ ] Create UrlResource from file path
  - [ ] Return Resource
  - [ ] Throw FileNotFoundException if not found

- [ ] 4. Implement deleteFile method
  - [ ] Method signature: `void deleteFile(String filename)`
  - [ ] Sanitize filename
  - [ ] Build file Path
  - [ ] Delete file using `Files.deleteIfExists()`
  - [ ] Log deletion for audit

- [ ] 5. Create custom exceptions
  - [ ] Create `exception/FileStorageException.java`
  - [ ] Create `exception/InvalidFileTypeException.java`
  - [ ] Create `exception/FileNotFoundException.java`

- [ ] 6. Add file validation helper method
  - [ ] Private method to validate file extension
  - [ ] Extract extension from filename
  - [ ] Check against allowed extensions
  - [ ] Throw InvalidFileTypeException if not allowed

- [ ] 7. Add filename sanitization helper
  - [ ] Remove any path separators (/, \)
  - [ ] Remove any parent directory references (..)
  - [ ] Ensure filename is not empty after sanitization

**Acceptance Criteria**:
- FileStorageService created
- storeFile validates and saves files
- Unique filenames generated
- File path traversal prevented
- loadFileAsResource retrieves files
- deleteFile removes files
- Custom exceptions defined
- Only allowed file types accepted

---

## PR #43: File Upload Controller

**PRD Reference**: Phase IV, Deliverable 2 - File Upload (File Upload Endpoint)

### Tasks

- [ ] 1. Create FileUploadResponse DTO
  - [ ] Create `model/dto/FileUploadResponse.java`
  - [ ] Add fields: filename, downloadUrl, size, uploadTime
  - [ ] Use Lombok @Data

- [ ] 2. Create FileController class
  - [ ] Create `controller/FileController.java`
  - [ ] Add `@RestController` annotation
  - [ ] Add `@RequestMapping("/api/files")` annotation
  - [ ] Inject FileStorageService

- [ ] 3. Implement POST /api/files/upload-resume endpoint
  - [ ] Add method with `@PostMapping("/upload-resume")` annotation
  - [ ] Method signature: `ResponseEntity<FileUploadResponse> uploadResume(@RequestParam("file") MultipartFile file)`
  - [ ] Call fileStorageService.storeFile(file)
  - [ ] Build download URL: `/api/files/download/{filename}`
  - [ ] Create FileUploadResponse with details
  - [ ] Return ResponseEntity.ok()

- [ ] 4. Implement GET /api/files/download/{filename} endpoint
  - [ ] Add method with `@GetMapping("/download/{filename}")` annotation
  - [ ] Method signature: `ResponseEntity<Resource> downloadFile(@PathVariable String filename)`
  - [ ] Call fileStorageService.loadFileAsResource(filename)
  - [ ] Set Content-Disposition header to "attachment; filename={filename}"
  - [ ] Detect content type (application/pdf, application/msword, etc.)
  - [ ] Return ResponseEntity with Resource body and headers

- [ ] 5. Add file size validation
  - [ ] Spring Boot's multipart config will handle this
  - [ ] Catch MaxUploadSizeExceededException in GlobalExceptionHandler
  - [ ] Return 400 with message "File size exceeds maximum limit of 5MB"

- [ ] 6. Secure endpoints (optional for now)
  - [ ] Upload should require authentication
  - [ ] Download might be public or require authentication
  - [ ] Update SecurityConfig if needed

**Acceptance Criteria**:
- FileController created
- Upload endpoint works
- Download endpoint works
- File size limit enforced
- Only authenticated users can upload (if secured)
- Files served with correct content type
- Download links work

---

## PR #44: Application Entity & Repository

**PRD Reference**: Phase IV, Deliverable 3 - Application Entity

### Tasks

- [ ] 1. Create ApplicationStatus enum
  - [ ] Create `model/entity/ApplicationStatus.java`
  - [ ] Add values: PENDING, REVIEWING, INTERVIEWED, ACCEPTED, REJECTED

- [ ] 2. Create Application entity
  - [ ] Create `model/entity/Application.java`
  - [ ] Add `@Entity` and `@Table(name = "application")`
  - [ ] Add id with `@Id` and `@GeneratedValue`
  - [ ] Add job relationship with `@ManyToOne(fetch = LAZY)` and `@JoinColumn(name = "job_id")`
  - [ ] Add user relationship with `@ManyToOne(fetch = LAZY)` and `@JoinColumn(name = "user_id")`
  - [ ] Add resumePath field (String) - stores filename from file upload
  - [ ] Add coverLetter field (TEXT column)
  - [ ] Add status field with `@Enumerated(STRING)`, default PENDING
  - [ ] Add appliedAt field with `@CreatedDate`
  - [ ] Add version field with `@Version`
  - [ ] Add `@Table` annotation with unique constraint on job_id + user_id
  - [ ] Generate getters/setters

- [ ] 3. Update Job entity to include applications relationship (optional)
  - [ ] Add `@OneToMany(mappedBy = "job")` for applications
  - [ ] Private List<Application> applications
  - [ ] This is optional - can navigate from Application to Job instead

- [ ] 4. Create ApplicationRepository interface
  - [ ] Create `repository/ApplicationRepository.java`
  - [ ] Extend `JpaRepository<Application, Long>`
  - [ ] Add method: `List<Application> findByUserId(Long userId)`
  - [ ] Add method: `Page<Application> findByJobId(Long jobId, Pageable pageable)`
  - [ ] Add method: `Optional<Application> findByJobIdAndUserId(Long jobId, Long userId)`
  - [ ] Add method: `boolean existsByJobIdAndUserId(Long jobId, Long userId)`

- [ ] 5. Test database schema generation
  - [ ] Start application
  - [ ] Verify `application` table created
  - [ ] Verify foreign keys to job and user tables
  - [ ] Verify unique constraint on (job_id, user_id)

**Acceptance Criteria**:
- ApplicationStatus enum created
- Application entity created
- Foreign key relationships configured
- Unique constraint prevents duplicate applications
- ApplicationRepository created with custom methods
- Database schema generated correctly

---

## PR #45: Application DTOs

**PRD Reference**: Phase IV, Deliverable 3 - Application DTOs

### Tasks

- [ ] 1. Create ApplicationDTO (for responses)
  - [ ] Create `model/dto/ApplicationDTO.java`
  - [ ] Add fields: id, jobId, jobTitle, companyName, userId, userEmail, resumePath, status, appliedAt
  - [ ] Include job and company details for convenience
  - [ ] Use Lombok @Data

- [ ] 2. Create ApplicationDetailDTO (for single application view)
  - [ ] Create `model/dto/ApplicationDetailDTO.java`
  - [ ] Extend or include fields from ApplicationDTO
  - [ ] Add field: coverLetter
  - [ ] Add field: downloadUrl (for resume)

- [ ] 3. Create ApplicationCreateDTO (for POST requests)
  - [ ] Create `model/dto/ApplicationCreateDTO.java`
  - [ ] Add fields: jobId, coverLetter, resumeFilename (optional if already uploaded)
  - [ ] Add validation: `@NotNull` on jobId
  - [ ] CoverLetter optional (can be null)

- [ ] 4. Create ApplicationUpdateStatusDTO (for recruiters)
  - [ ] Create `model/dto/ApplicationUpdateStatusDTO.java`
  - [ ] Add field: status (ApplicationStatus)
  - [ ] Add validation: `@NotNull`

- [ ] 5. Create Application mapper utility
  - [ ] Create `util/ApplicationMapper.java`
  - [ ] Add method: `ApplicationDTO toDTO(Application entity)`
  - [ ] Add method: `ApplicationDetailDTO toDetailDTO(Application entity)`
  - [ ] Include job and user details in DTOs
  - [ ] Build download URL for resume

**Acceptance Criteria**:
- All Application DTOs created
- Validation annotations applied
- Mapper utility created
- DTOs include necessary related data (job, company, user)

---

## PR #46: Application Service

**PRD Reference**: Phase IV, Deliverable 3 - Application Business Logic

### Tasks

- [ ] 1. Create ApplicationService class
  - [ ] Create `service/ApplicationService.java`
  - [ ] Add `@Service` and `@Transactional` annotations
  - [ ] Inject ApplicationRepository
  - [ ] Inject JobRepository
  - [ ] Inject UserRepository
  - [ ] Inject ApplicationMapper

- [ ] 2. Implement createApplication method
  - [ ] Method signature: `ApplicationDTO createApplication(ApplicationCreateDTO dto, String userEmail)`
  - [ ] Fetch user by email (from authentication)
  - [ ] Fetch job by jobId
  - [ ] Check if job exists, throw JobNotFoundException if not
  - [ ] Check if job is active, throw exception if inactive/expired
  - [ ] Check if user already applied: `existsByJobIdAndUserId`
  - [ ] If already applied, throw DuplicateApplicationException
  - [ ] Create Application entity from DTO
  - [ ] Set job, user, status=PENDING, appliedAt=now
  - [ ] Save application
  - [ ] Return ApplicationDTO

- [ ] 3. Implement getMyApplications method
  - [ ] Method signature: `List<ApplicationDTO> getMyApplications(String userEmail)`
  - [ ] Fetch user by email
  - [ ] Fetch applications by userId
  - [ ] Map to DTOs
  - [ ] Return list

- [ ] 4. Implement getApplicationsForJob method (recruiter only)
  - [ ] Method signature: `Page<ApplicationDTO> getApplicationsForJob(Long jobId, Pageable pageable)`
  - [ ] Fetch applications by jobId with pagination
  - [ ] Map to DTOs
  - [ ] Return Page<ApplicationDTO>
  - [ ] Note: Authorization check will be in controller

- [ ] 5. Implement updateApplicationStatus method (recruiter only)
  - [ ] Method signature: `ApplicationDTO updateApplicationStatus(Long applicationId, ApplicationStatus newStatus)`
  - [ ] Fetch application by id
  - [ ] Throw exception if not found
  - [ ] Update status
  - [ ] Save application
  - [ ] Return updated DTO

- [ ] 6. Implement withdrawApplication method
  - [ ] Method signature: `void withdrawApplication(Long applicationId, String userEmail)`
  - [ ] Fetch application by id
  - [ ] Verify application belongs to user (compare user emails)
  - [ ] If not, throw UnauthorizedException
  - [ ] Delete application
  - [ ] Note: Alternative is to add "WITHDRAWN" status instead of deleting

- [ ] 7. Create custom exceptions
  - [ ] Create `exception/DuplicateApplicationException.java`
  - [ ] Create `exception/InactiveJobException.java`
  - [ ] Create `exception/UnauthorizedException.java` (if not already exists)

**Acceptance Criteria**:
- ApplicationService created
- Create application validates job is active
- Duplicate applications prevented
- User can view their own applications
- Recruiter can view applications for job
- Status updates work
- User can withdraw application
- All exceptions handled

---

## PR #47: Application Controller

**PRD Reference**: Phase IV, Deliverable 3 - Application Endpoints

### Tasks

- [ ] 1. Create ApplicationController class
  - [ ] Create `controller/ApplicationController.java`
  - [ ] Add `@RestController` annotation
  - [ ] Add `@RequestMapping("/api/applications")` annotation
  - [ ] Inject ApplicationService

- [ ] 2. Implement POST /api/applications endpoint (apply to job)
  - [ ] Add `@PostMapping` annotation
  - [ ] Method signature: `ResponseEntity<ApplicationDTO> applyToJob(@Valid @RequestBody ApplicationCreateDTO dto, Authentication auth)`
  - [ ] Get user email from auth.getName()
  - [ ] Call applicationService.createApplication(dto, userEmail)
  - [ ] Return ResponseEntity.status(HttpStatus.CREATED).body(result)

- [ ] 3. Implement POST /api/applications/with-resume endpoint
  - [ ] Add `@PostMapping("/with-resume")` annotation
  - [ ] Accept `@RequestPart ApplicationCreateDTO dto` and `@RequestPart MultipartFile resume`
  - [ ] Upload resume using FileStorageService
  - [ ] Set resume filename in DTO
  - [ ] Call applicationService.createApplication()
  - [ ] Return 201 with ApplicationDTO

- [ ] 4. Implement GET /api/applications/my-applications endpoint
  - [ ] Add `@GetMapping("/my-applications")` annotation
  - [ ] Method signature: `ResponseEntity<List<ApplicationDTO>> getMyApplications(Authentication auth)`
  - [ ] Get user email from auth
  - [ ] Call applicationService.getMyApplications(userEmail)
  - [ ] Return ResponseEntity.ok()

- [ ] 5. Implement GET /api/applications/job/{jobId} endpoint (recruiter only)
  - [ ] Add `@GetMapping("/job/{jobId}")` annotation
  - [ ] Add `@PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")` annotation
  - [ ] Accept pagination parameters
  - [ ] Method signature: `ResponseEntity<Page<ApplicationDTO>> getApplicationsForJob(@PathVariable Long jobId, ...)`
  - [ ] Call applicationService.getApplicationsForJob()
  - [ ] Return Page<ApplicationDTO>

- [ ] 6. Implement PATCH /api/applications/{id}/status endpoint (recruiter only)
  - [ ] Add `@PatchMapping("/{id}/status")` annotation
  - [ ] Add `@PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")` annotation
  - [ ] Method signature: `ResponseEntity<ApplicationDTO> updateStatus(@PathVariable Long id, @Valid @RequestBody ApplicationUpdateStatusDTO dto)`
  - [ ] Call applicationService.updateApplicationStatus()
  - [ ] Return ResponseEntity.ok()

- [ ] 7. Implement DELETE /api/applications/{id} endpoint (withdraw)
  - [ ] Add `@DeleteMapping("/{id}")` annotation
  - [ ] Method signature: `ResponseEntity<Void> withdrawApplication(@PathVariable Long id, Authentication auth)`
  - [ ] Call applicationService.withdrawApplication(id, auth.getName())
  - [ ] Return ResponseEntity.noContent().build()

- [ ] 8. Add authorization logic for job ownership (optional enhancement)
  - [ ] Recruiters should only see applications for their company's jobs
  - [ ] Can add check in service layer or use custom security expression
  - [ ] For now, any RECRUITER can see any applications (simplification)

**Acceptance Criteria**:
- ApplicationController created
- Apply to job endpoint works
- Apply with resume upload works
- User can view their applications
- Recruiter can view applications for jobs
- Recruiter can update application status
- User can withdraw application
- Authorization enforced with @PreAuthorize

---

## PR #48: Application Exception Handling

**PRD Reference**: Phase IV - Exception Handling for Applications

### Tasks

- [ ] 1. Update GlobalExceptionHandler
  - [ ] Open `exception/GlobalExceptionHandler.java`

- [ ] 2. Handle DuplicateApplicationException
  - [ ] Add `@ExceptionHandler(DuplicateApplicationException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.CONFLICT)` (409)
  - [ ] Return ErrorResponse with message "You have already applied to this job"

- [ ] 3. Handle InactiveJobException
  - [ ] Add `@ExceptionHandler(InactiveJobException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.BAD_REQUEST)` (400)
  - [ ] Return ErrorResponse with message "Cannot apply to inactive or expired job"

- [ ] 4. Handle UnauthorizedException (if created)
  - [ ] Add `@ExceptionHandler(UnauthorizedException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.FORBIDDEN)` (403)
  - [ ] Return ErrorResponse

- [ ] 5. Handle FileStorageException
  - [ ] Add `@ExceptionHandler(FileStorageException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)` (500)
  - [ ] Return ErrorResponse with message about file storage failure

- [ ] 6. Handle InvalidFileTypeException
  - [ ] Add `@ExceptionHandler(InvalidFileTypeException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.BAD_REQUEST)` (400)
  - [ ] Return ErrorResponse with message "Invalid file type. Allowed types: PDF, DOC, DOCX"

- [ ] 7. Handle MaxUploadSizeExceededException
  - [ ] Add `@ExceptionHandler(MaxUploadSizeExceededException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.BAD_REQUEST)` (400)
  - [ ] Return ErrorResponse with message "File size exceeds maximum limit of 5MB"

**Acceptance Criteria**:
- All application-related exceptions handled
- Proper HTTP status codes returned
- Clear error messages for users
- File upload errors handled gracefully

---

## PR #49: Scheduled Task for Auto-Expiring Jobs

**PRD Reference**: Phase IV, Deliverable 4 - Scheduled Task

### Tasks

- [ ] 1. Enable scheduling in Spring Boot
  - [ ] Create `config/SchedulingConfig.java`
  - [ ] Add `@Configuration` annotation
  - [ ] Add `@EnableScheduling` annotation

- [ ] 2. Create JobExpirationTask class
  - [ ] Create `task/JobExpirationTask.java`
  - [ ] Add `@Component` annotation
  - [ ] Inject JobRepository
  - [ ] Add SLF4J logger

- [ ] 3. Implement scheduled method with @Scheduled annotation
  - [ ] Add method with `@Scheduled(cron = "0 0 2 * * ?")` (runs daily at 2 AM)
  - [ ] Add `@Transactional` annotation
  - [ ] Method signature: `void expireOldJobs()`

- [ ] 4. Implement job expiration logic - Option 1 (query-based)
  - [ ] Add custom update query to JobRepository:
    ```java
    @Query("UPDATE Job j SET j.isActive = false WHERE j.isActive = true " +
           "AND j.expiryDate IS NOT NULL AND j.expiryDate < CURRENT_TIMESTAMP")
    @Modifying
    int deactivateExpiredJobs();
    ```
  - [ ] Call this method from scheduled task
  - [ ] Log number of jobs deactivated

- [ ] 5. Implement job expiration logic - Option 2 (fetch and update)
  - [ ] Fetch all active jobs with non-null expiryDate
  - [ ] Filter jobs where expiryDate is before now
  - [ ] Set isActive to false for each
  - [ ] Save all jobs using saveAll
  - [ ] Log count of expired jobs
  - [ ] Note: Choose one option, query-based is more efficient

- [ ] 6. Add logging
  - [ ] Log when task starts: "Starting job expiration check"
  - [ ] Log results: "Expired {count} jobs"
  - [ ] Log errors if any occur

- [ ] 7. Configure cron expression (optional customization)
  - [ ] Current: Daily at 2 AM
  - [ ] Can make configurable via properties
  - [ ] Add `@Scheduled(cron = "${job.expiration.cron}")` and property

- [ ] 8. Test scheduled task
  - [ ] Create jobs with past expiry dates
  - [ ] Manually trigger task or wait for scheduled time
  - [ ] Verify jobs are deactivated
  - [ ] Check logs for execution

**Acceptance Criteria**:
- Scheduling enabled
- JobExpirationTask created
- Scheduled method runs daily at 2 AM
- Expired jobs automatically deactivated
- Job count logged
- Errors handled and logged
- Can test manually if needed

---

## Phase IV Part 2 Manual Testing Checklist

### File Upload
- [ ] Test: POST /api/files/upload-resume with PDF file succeeds
- [ ] Test: Upload with DOC/DOCX file succeeds
- [ ] Test: Upload with invalid file type (e.g., .txt) returns 400
- [ ] Test: Upload file > 5MB returns 400
- [ ] Test: Download uploaded file works
- [ ] Test: Downloaded file is not corrupted
- [ ] Verify files stored in configured directory
- [ ] Verify unique filenames generated

### Applications - Create
- [ ] Test: POST /api/applications (apply to job) succeeds for authenticated user
- [ ] Test: Apply to same job twice returns 409 Conflict
- [ ] Test: Apply to inactive job returns 400
- [ ] Test: Apply to expired job returns 400
- [ ] Test: Apply to non-existent job returns 404
- [ ] Test: Apply without authentication returns 401
- [ ] Test: POST /api/applications/with-resume uploads resume and creates application

### Applications - View
- [ ] Test: GET /api/applications/my-applications returns user's applications
- [ ] Test: User A cannot see User B's applications
- [ ] Test: GET /api/applications/job/{jobId} returns applications for job (recruiter only)
- [ ] Test: JOB_SEEKER cannot access /api/applications/job/{jobId} (returns 403)
- [ ] Test: RECRUITER can access /api/applications/job/{jobId}

### Applications - Update
- [ ] Test: RECRUITER can PATCH /api/applications/{id}/status
- [ ] Test: Update status to REVIEWING succeeds
- [ ] Test: Update status to ACCEPTED succeeds
- [ ] Test: JOB_SEEKER cannot update status (returns 403)

### Applications - Delete
- [ ] Test: User can DELETE their own application
- [ ] Test: User cannot delete another user's application
- [ ] Test: Delete non-existent application returns 404

### Scheduled Task
- [ ] Create job with expiry date in the past
- [ ] Wait for scheduled task to run (or trigger manually if possible)
- [ ] Verify job is deactivated (isActive = false)
- [ ] Check application logs for task execution
- [ ] Verify jobs without expiry date not affected
- [ ] Verify active jobs with future expiry not affected

### Integration Testing
- [ ] Complete flow: Register → Login → Upload Resume → Apply to Job
- [ ] Recruiter flow: Login → Create Job → View Applications → Update Status
- [ ] Verify resume download link in ApplicationDetailDTO works
- [ ] Test pagination on /api/applications/job/{jobId}

---

## Phase IV Part 2 Completion Checklist

- [ ] All PRs merged
- [ ] File upload configuration done
- [ ] File storage service implemented
- [ ] File upload controller working
- [ ] Resume upload and download working
- [ ] File validation working (type and size)
- [ ] Application entity created
- [ ] Application repository with custom queries
- [ ] Application service with all business logic
- [ ] Application controller with all endpoints
- [ ] Apply to job working
- [ ] View applications working
- [ ] Update application status working
- [ ] Withdraw application working
- [ ] Duplicate applications prevented
- [ ] Authorization enforced (recruiters vs job seekers)
- [ ] Scheduled task configured
- [ ] Job expiration task running
- [ ] Expired jobs auto-deactivated
- [ ] All exception scenarios handled
- [ ] All manual tests passing

**When all items checked**: Phase IV is complete! The Job Board API is fully functional with all advanced features implemented.

---

## Final Project Checklist

Once Phase IV Part 2 is complete, verify the entire project:

### Functionality
- [ ] All CRUD operations work (Jobs, Companies, Users, Applications)
- [ ] Search and filtering working
- [ ] Pagination working
- [ ] Authentication and authorization working
- [ ] File upload/download working
- [ ] Scheduled tasks running
- [ ] All business rules enforced

### Code Quality
- [ ] Clean architecture maintained (Controller → Service → Repository)
- [ ] No code duplication
- [ ] Proper exception handling throughout
- [ ] Validation on all inputs
- [ ] DTOs used for all API communication
- [ ] Entities properly mapped

### Testing
- [ ] Unit tests passing
- [ ] Integration tests passing
- [ ] API tests passing
- [ ] Test coverage meets goals (80%+ overall)

### Security
- [ ] Passwords hashed (BCrypt)
- [ ] JWT tokens secure
- [ ] Authorization enforced
- [ ] File upload validated
- [ ] No SQL injection vulnerabilities
- [ ] No sensitive data in logs

### Database
- [ ] Schema matches design
- [ ] Indexes created
- [ ] Foreign keys enforced
- [ ] Cascade deletes configured
- [ ] Optimistic locking working

### Documentation
- [ ] README with setup instructions
- [ ] API endpoints documented (or Swagger/OpenAPI added)
- [ ] Environment variables documented
- [ ] Database schema documented

### Deployment Ready
- [ ] Docker Compose working
- [ ] Environment-specific configs
- [ ] Logs configured properly
- [ ] Application properties documented

**Congratulations!** 🎉 You've built a production-ready Job Board API with Spring Boot!
