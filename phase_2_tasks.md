# Phase II: Advanced Routes - Implementation Tasks

**Reference**: See PRD Section "Phase II: Advanced Routes"

**Overview**: Add search and filtering capabilities, implement pagination, add route for active jobs only, add soft delete (deactivate) functionality.

---

## PR #13: Add Pagination Support to Existing Endpoints

**PRD Reference**: Phase II, Deliverable 4 - Pagination on Existing Endpoints

### Tasks

- [x] 1. Update JobService.getAllJobs to support pagination

  - [x] Change method signature to accept `Pageable pageable` parameter
  - [x] Change return type from `List<JobDTO>` to `Page<JobDTO>`
  - [x] Update repository call to `repository.findAll(pageable)`
  - [x] Map Page<Job> to Page<JobDTO> using `map()` method
  - [x] Return Page<JobDTO>

- [x] 2. Update JobController.getAllJobs to accept pagination parameters

  - [x] Add `@RequestParam` for page (default 0)
  - [x] Add `@RequestParam` for size (default 20)
  - [x] Add `@RequestParam` for sort (default "postedDate,desc")
  - [x] Change return type to `ResponseEntity<Page<JobDTO>>`
  - [x] Create PageRequest with: `PageRequest.of(page, size, Sort.by(parseSort(sort)))`
  - [x] Pass Pageable to service method
  - [x] Return ResponseEntity.ok() with Page

- [x] 3. Add sort parsing utility method

  - [x] Create private method `parseSort(String sort)` in controller
  - [x] Split sort string by comma (e.g., "postedDate,desc" → ["postedDate", "desc"])
  - [x] Create Sort.Order with field name and direction
  - [x] Handle multiple sort fields if needed
  - [x] Return Sort object

- [x] 4. Add validation for pagination parameters

  - [x] Add `@Min(0)` annotation to page parameter
  - [x] Add `@Min(1) @Max(100)` annotations to size parameter
  - [x] Add validation for sort field names (prevent SQL injection)

- [x] 5. Update CompanyService.getAllCompanies for pagination

  - [x] Add Pageable parameter
  - [x] Change return type to Page<CompanyDTO>
  - [x] Update repository call and mapping

- [x] 6. Update CompanyController.getAllCompanies for pagination

  - [x] Add same pagination parameters as JobController
  - [x] Update method signature and return type
  - [x] Create PageRequest and pass to service

- [x] 7. Test pagination manually
  - [x] Create 25+ jobs in database
  - [x] Test: GET /api/jobs?page=0&size=10 returns first 10 jobs
  - [x] Test: GET /api/jobs?page=1&size=10 returns next 10 jobs
  - [x] Test: Verify totalElements, totalPages in response
  - [x] Test: Sort by different fields (title, postedDate, salary)
  - [x] Test: Invalid page/size returns 400

**Acceptance Criteria**:

- Pagination works on GET /api/jobs
- Pagination works on GET /api/companies
- Page response includes content, pagination metadata
- Sorting works on multiple fields
- Page size limited to max 100
- Invalid parameters return 400 with validation errors

---

## PR #14: Add Active Jobs Repository Query

**PRD Reference**: Phase II, Deliverable 2 - Active Jobs Endpoint (Repository Method)

### Tasks

- [x] 1. Add findActiveJobs query to JobRepository

  - [x] Open `repository/JobRepository.java`
  - [x] Add method with `@Query` annotation
  - [x] Write JPQL: `"SELECT j FROM Job j WHERE j.isActive = true AND (j.expiryDate IS NULL OR j.expiryDate > CURRENT_TIMESTAMP)"`
  - [x] Method signature: `Page<Job> findActiveJobs(Pageable pageable)`

- [x] 2. Add test data setup (manual verification)

  - [x] Create jobs with isActive = true, expiryDate = null
  - [x] Create jobs with isActive = true, expiryDate = future
  - [x] Create jobs with isActive = false
  - [x] Create jobs with isActive = true, expiryDate = past

- [x] 3. Test query in application
  - [x] Add temporary test endpoint or use database client
  - [x] Verify only active, non-expired jobs returned
  - [x] Verify query uses proper SQL (check logs)

**Acceptance Criteria**:

- Custom query written and compiles
- Query returns only active jobs with null or future expiry dates
- Query supports pagination
- SQL logged correctly shows proper WHERE clause

---

## PR #15: Add Search/Filter Repository Query

**PRD Reference**: Phase II, Deliverable 1 - Search & Filter Endpoint (Repository Method)

### Tasks

- [ ] 1. Add searchJobs method to JobRepository

  - [ ] Open `repository/JobRepository.java`
  - [ ] Add method with `@Query` annotation
  - [ ] Method signature: `Page<Job> searchJobs(` with parameters for each filter
  - [ ] Add @Param annotations for each parameter

- [ ] 2. Build JPQL query for keyword search

  - [ ] Add WHERE clause for keyword parameter
  - [ ] Search in both title and description using LIKE
  - [ ] Use LOWER() for case-insensitive search
  - [ ] Use CONCAT('%', :keyword, '%') for partial matching
  - [ ] Handle null keyword with: `:keyword IS NULL OR ...`

- [ ] 3. Add location filter to query

  - [ ] Add AND clause for location parameter
  - [ ] Use LOWER() and LIKE for partial matching
  - [ ] Handle null with: `:location IS NULL OR ...`

- [ ] 4. Add company filter to query

  - [ ] Add AND clause for companyId
  - [ ] Compare: `j.company.id = :companyId`
  - [ ] Handle null with: `:companyId IS NULL OR ...`

- [ ] 5. Add enum filters (jobType, experienceLevel, remoteOption)

  - [ ] Add AND clauses for each enum filter
  - [ ] Use exact match: `j.jobType = :jobType`
  - [ ] Handle null for each with: `:jobType IS NULL OR ...`

- [ ] 6. Add salary range filters

  - [ ] For minSalary: `j.salaryMax >= :minSalary` (job's max salary must meet minimum requirement)
  - [ ] For maxSalary: `j.salaryMin <= :maxSalary` (job's min salary must be within budget)
  - [ ] Handle null for each

- [ ] 7. Add isActive filter

  - [ ] Add AND clause: `j.isActive = :isActive`
  - [ ] Handle null with: `:isActive IS NULL OR ...`

- [ ] 8. Complete method signature

  ```java
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
  ```

- [ ] 9. Test query manually
  - [ ] Test with only keyword
  - [ ] Test with only location
  - [ ] Test with combination of filters
  - [ ] Test with null values for optional filters
  - [ ] Verify SQL query in logs is correct

**Acceptance Criteria**:

- searchJobs method added to repository
- Query handles all filter combinations
- Null parameters properly handled
- Case-insensitive search works
- Partial matching works for keyword and location
- Salary range logic correct
- Query compiles and executes successfully

---

## PR #16: Active Jobs Service & Controller

**PRD Reference**: Phase II, Deliverable 2 - Active Jobs Endpoint

### Tasks

- [ ] 1. Add getActiveJobs method to JobService

  - [ ] Method signature: `Page<JobDTO> getActiveJobs(Pageable pageable)`
  - [ ] Add `@Transactional(readOnly = true)` annotation
  - [ ] Call `jobRepository.findActiveJobs(pageable)`
  - [ ] Map Page<Job> to Page<JobDTO>
  - [ ] Return Page<JobDTO>

- [ ] 2. Add getActiveJobs endpoint to JobController

  - [ ] Add method with `@GetMapping("/active")` annotation
  - [ ] Accept pagination parameters (page, size, sort)
  - [ ] Method signature: `ResponseEntity<Page<JobDTO>> getActiveJobs(...)`
  - [ ] Create Pageable from parameters
  - [ ] Call service.getActiveJobs(pageable)
  - [ ] Return ResponseEntity.ok() with result

- [ ] 3. Test active jobs endpoint
  - [ ] Create test data (active, inactive, expired jobs)
  - [ ] Test: GET /api/jobs/active returns only active, non-expired jobs
  - [ ] Test: Pagination works with active jobs
  - [ ] Test: Verify inactive jobs not included
  - [ ] Test: Verify expired jobs not included

**Acceptance Criteria**:

- Service method implemented
- Controller endpoint created at /api/jobs/active
- Only returns jobs where isActive=true and not expired
- Pagination works correctly
- Returns 200 with Page<JobDTO>

---

## PR #17: Search Jobs Service & Controller

**PRD Reference**: Phase II, Deliverable 1 - Search & Filter Endpoint

### Tasks

- [ ] 1. Add searchJobs method to JobService

  - [ ] Method signature: `Page<JobDTO> searchJobs(` with all filter parameters + Pageable
  - [ ] Add `@Transactional(readOnly = true)` annotation
  - [ ] Call `jobRepository.searchJobs(...)` with all parameters
  - [ ] Map Page<Job> to Page<JobDTO>
  - [ ] Return Page<JobDTO>

- [ ] 2. Add searchJobs endpoint to JobController

  - [ ] Add method with `@GetMapping("/search")` annotation
  - [ ] Accept all filter parameters as `@RequestParam(required = false)`
    - [ ] keyword (String)
    - [ ] location (String)
    - [ ] companyId (Long)
    - [ ] jobType (JobType)
    - [ ] experienceLevel (ExperienceLevel)
    - [ ] remoteOption (RemoteOption)
    - [ ] minSalary (BigDecimal)
    - [ ] maxSalary (BigDecimal)
    - [ ] isActive (Boolean, default true)
  - [ ] Accept pagination parameters (page, size, sort)
  - [ ] Method signature: `ResponseEntity<Page<JobDTO>> searchJobs(...)`
  - [ ] Create Pageable from pagination parameters
  - [ ] Call service.searchJobs(...) with all parameters
  - [ ] Return ResponseEntity.ok() with result

- [ ] 3. Add parameter validation

  - [ ] Add `@Min(0)` to minSalary and maxSalary if present
  - [ ] Validate enum values are parsed correctly (Spring does this automatically)

- [ ] 4. Handle enum parsing errors
  - [ ] Add exception handler for MethodArgumentTypeMismatchException
  - [ ] Return 400 with message explaining invalid enum value

**Acceptance Criteria**:

- Service method accepts all filter parameters
- Controller endpoint created at /api/jobs/search
- All filters work independently and in combination
- Pagination works with search
- Null/missing parameters handled correctly
- Invalid enum values return 400 with clear message
- Returns 200 with Page<JobDTO>

---

## PR #18: Deactivate Job Feature

**PRD Reference**: Phase II, Deliverable 3 - Deactivate Job Endpoint

### Tasks

- [ ] 1. Create DeactivateResponse DTO

  - [ ] Create `model/dto/DeactivateResponse.java`
  - [ ] Add fields: message (String), jobId (Long)
  - [ ] Add constructors
  - [ ] Use Lombok @Data

- [ ] 2. Add deactivateJob method to JobService

  - [ ] Method signature: `void deactivateJob(Long id)`
  - [ ] Add `@Transactional` annotation (not read-only)
  - [ ] Fetch job by id using repository.findById()
  - [ ] Throw JobNotFoundException if not found
  - [ ] Set job.setIsActive(false)
  - [ ] Save job using repository.save()

- [ ] 3. Add deactivateJob endpoint to JobController

  - [ ] Add method with `@PostMapping("/{id}/deactivate")` annotation
  - [ ] Method signature: `ResponseEntity<DeactivateResponse> deactivateJob(@PathVariable Long id)`
  - [ ] Call service.deactivateJob(id)
  - [ ] Create DeactivateResponse with message and jobId
  - [ ] Return ResponseEntity.ok() with response

- [ ] 4. Test deactivate functionality
  - [ ] Create active job
  - [ ] Test: POST /api/jobs/{id}/deactivate sets isActive to false
  - [ ] Verify job no longer appears in /api/jobs/active
  - [ ] Verify job still appears in /api/jobs (unless filtered)
  - [ ] Test: Deactivate non-existent job returns 404
  - [ ] Test: Deactivate already inactive job succeeds (idempotent)

**Acceptance Criteria**:

- DeactivateResponse DTO created
- Service method sets isActive to false
- Controller endpoint created at POST /api/jobs/{id}/deactivate
- Returns 200 with success message and jobId
- Deactivated jobs excluded from active endpoint
- Endpoint is idempotent (can deactivate already inactive job)

---

## PR #19: Enhanced Exception Handling for Phase II

**PRD Reference**: Phase II - Additional error scenarios

### Tasks

- [ ] 1. Add handler for MethodArgumentTypeMismatchException

  - [ ] Open `exception/GlobalExceptionHandler.java`
  - [ ] Add method with `@ExceptionHandler(MethodArgumentTypeMismatchException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.BAD_REQUEST)`
  - [ ] Extract parameter name and required type from exception
  - [ ] Return ErrorResponse with message: "Invalid value for parameter '{name}'. Expected type: {type}"

- [ ] 2. Add handler for ConstraintViolationException (query param validation)

  - [ ] Add method with `@ExceptionHandler(ConstraintViolationException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.BAD_REQUEST)`
  - [ ] Extract constraint violations
  - [ ] Build map of parameter names to error messages
  - [ ] Return ValidationErrorResponse

- [ ] 3. Improve validation error messages

  - [ ] Update existing MethodArgumentNotValidException handler
  - [ ] Ensure field names are clear in error response
  - [ ] Ensure error messages are user-friendly

- [ ] 4. Test error handling
  - [ ] Test: Invalid enum value in search returns 400
  - [ ] Test: Invalid page/size parameters return 400
  - [ ] Test: Invalid sort field returns 400 or is handled gracefully
  - [ ] Verify all error responses follow standard format

**Acceptance Criteria**:

- Type mismatch errors return 400 with clear message
- Constraint violations return 400 with field-level errors
- All new error scenarios properly handled
- Error response format consistent across all handlers

---

## PR #20: Repository Method for Finding Jobs by Company

**PRD Reference**: Phase II - Support for company-specific queries (already in Phase I, enhancing)

### Tasks

- [ ] 1. Enhance existing findByCompanyId method

  - [ ] Update method signature to return Page instead of List
  - [ ] Method signature: `Page<Job> findByCompanyId(Long companyId, Pageable pageable)`
  - [ ] No @Query annotation needed (Spring Data will generate it)

- [ ] 2. Add getJobsByCompany method to JobService

  - [ ] Method signature: `Page<JobDTO> getJobsByCompany(Long companyId, Pageable pageable)`
  - [ ] Add `@Transactional(readOnly = true)` annotation
  - [ ] Call repository.findByCompanyId(companyId, pageable)
  - [ ] Map to Page<JobDTO>
  - [ ] Return result

- [ ] 3. Add getJobsByCompany endpoint to JobController (optional)

  - [ ] Add method with `@GetMapping` and `@RequestParam("companyId")`
  - [ ] Or use existing search endpoint with companyId parameter
  - [ ] Decision: Use search endpoint, no separate endpoint needed

- [ ] 4. Test company filter in search
  - [ ] Create multiple companies with jobs
  - [ ] Test: GET /api/jobs/search?companyId=1 returns only that company's jobs
  - [ ] Test: Pagination works with company filter

**Acceptance Criteria**:

- findByCompanyId supports pagination
- Service method created
- Company filter works in search endpoint
- No duplicate endpoints created

---

## Phase II Manual Testing Checklist

### Pagination Testing

#### Jobs Pagination

- [ ] Create 30+ jobs in database
- [ ] Test: GET /api/jobs?page=0&size=10 returns 10 jobs, correct totalElements
- [ ] Test: GET /api/jobs?page=1&size=10 returns next 10 jobs
- [ ] Test: GET /api/jobs?page=2&size=10 returns remaining jobs
- [ ] Test: GET /api/jobs?page=10&size=10 returns empty content (beyond last page)
- [ ] Test: GET /api/jobs?size=100 returns max 100 jobs
- [ ] Test: GET /api/jobs?size=101 returns 400 (exceeds max)
- [ ] Test: GET /api/jobs?page=-1 returns 400 (invalid page)

#### Sorting

- [ ] Test: sort=postedDate,desc (newest first)
- [ ] Test: sort=postedDate,asc (oldest first)
- [ ] Test: sort=title,asc (alphabetical by title)
- [ ] Test: sort=salaryMax,desc (highest paying first)
- [ ] Test: Invalid sort field returns 400 or defaults gracefully

#### Companies Pagination

- [ ] Test pagination on GET /api/companies works similarly to jobs

### Search & Filter Testing

#### Keyword Search

- [ ] Create jobs with "Java" in title, "Python" in description
- [ ] Test: keyword=java returns jobs with "Java" in title or description
- [ ] Test: keyword=JAVA (uppercase) still matches (case-insensitive)
- [ ] Test: keyword=dev returns jobs with "Developer", "Development", etc.
- [ ] Test: keyword=xyz (non-existent) returns empty results

#### Location Filter

- [ ] Create jobs in "New York", "San Francisco", "Remote"
- [ ] Test: location=New York returns NYC jobs (case-insensitive)
- [ ] Test: location=york returns jobs with "York" anywhere in location
- [ ] Test: location=Remote returns remote jobs

#### Company Filter

- [ ] Create multiple companies with jobs
- [ ] Test: companyId=1 returns only jobs for company 1
- [ ] Test: companyId=999 (non-existent) returns empty results

#### Job Type Filter

- [ ] Create jobs with different job types
- [ ] Test: jobType=FULL_TIME returns only full-time jobs
- [ ] Test: jobType=CONTRACT returns only contract jobs
- [ ] Test: jobType=INVALID returns 400 with error message

#### Experience Level Filter

- [ ] Test: experienceLevel=ENTRY returns only entry-level jobs
- [ ] Test: experienceLevel=SENIOR returns only senior jobs

#### Remote Option Filter

- [ ] Test: remoteOption=REMOTE returns only remote jobs
- [ ] Test: remoteOption=ONSITE returns only onsite jobs
- [ ] Test: remoteOption=HYBRID returns only hybrid jobs

#### Salary Range Filters

- [ ] Create jobs with various salary ranges
- [ ] Test: minSalary=100000 returns jobs with salaryMax >= 100000
- [ ] Test: maxSalary=80000 returns jobs with salaryMin <= 80000
- [ ] Test: minSalary=80000&maxSalary=120000 returns jobs within range
- [ ] Test: minSalary=200000 returns only high-paying jobs

#### Active Filter

- [ ] Create active and inactive jobs
- [ ] Test: isActive=true returns only active jobs
- [ ] Test: isActive=false returns only inactive jobs
- [ ] Test: No isActive param defaults to true or returns all (check implementation)

#### Combined Filters

- [ ] Test: keyword=java&location=New York&jobType=FULL_TIME&experienceLevel=SENIOR
- [ ] Test: companyId=1&isActive=true&minSalary=100000
- [ ] Test: All filters combined returns correct subset
- [ ] Verify SQL query in logs is optimized

#### Empty Results

- [ ] Test filters that match no jobs return empty page (not 404)
- [ ] Verify response structure still correct with empty content array

### Active Jobs Endpoint Testing

#### Active Jobs Query

- [ ] Create jobs: active with no expiry, active with future expiry, inactive, active with past expiry
- [ ] Test: GET /api/jobs/active returns only active, non-expired jobs
- [ ] Verify inactive jobs excluded
- [ ] Verify expired jobs excluded
- [ ] Verify jobs with future expiry included
- [ ] Test pagination on active jobs

### Deactivate Job Testing

#### Deactivate Endpoint

- [ ] Create active job
- [ ] Test: POST /api/jobs/{id}/deactivate sets isActive to false
- [ ] Verify response contains success message and jobId
- [ ] Test: GET /api/jobs/{id} still works (job not deleted)
- [ ] Test: GET /api/jobs/active no longer includes deactivated job
- [ ] Test: Deactivating non-existent job returns 404
- [ ] Test: Deactivating already inactive job succeeds (idempotent)

### Error Handling Testing

#### Invalid Parameters

- [ ] Test: jobType=INVALID_TYPE returns 400 with clear error
- [ ] Test: experienceLevel=123 returns 400
- [ ] Test: page=-1 returns 400
- [ ] Test: size=0 returns 400
- [ ] Test: size=1000 returns 400 (exceeds max)

#### Edge Cases

- [ ] Test: keyword with special characters handled correctly
- [ ] Test: location with special characters handled correctly
- [ ] Test: Empty string for keyword/location (should return all or be treated as null)

---

## Phase II Completion Checklist

- [ ] All PRs merged and code reviewed
- [ ] All manual tests passing
- [ ] Pagination works on all list endpoints
- [ ] Search endpoint supports all filters
- [ ] Active jobs endpoint returns correct results
- [ ] Deactivate functionality works correctly
- [ ] Error handling covers all new scenarios
- [ ] SQL queries optimized (check logs for N+1 issues)
- [ ] Invalid parameters return clear error messages
- [ ] Empty search results handled gracefully
- [ ] Response format consistent across all endpoints
- [ ] Page metadata (totalElements, totalPages, etc.) correct
- [ ] Max page size enforced
- [ ] All enum values validated properly

**When all items checked**: Phase II is complete! Proceed to Phase III (Testing).
