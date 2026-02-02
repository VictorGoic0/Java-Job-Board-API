# Active Context: Job Board API

## Current Focus

PR #9 and PR #10 complete. Next: PR #11 (Company Controller).

## Recent Changes

- PR #10 completed: JobService (@Service, @Transactional); getAllJobs (findAllWithCompany JOIN FETCH), getJobById (JobDetailDTO), createJob (validate company), updateJob (validate company if companyId present), deleteJob; JobRepository.findAllWithCompany() for N+1 avoidance.
- PR #9 completed: CompanyService (@Service, @Transactional), getAllCompanies, getCompanyById, createCompany, updateCompany (partial via mapper), deleteCompany; CompanyRepository and CompanyMapper injected.
- PR #8 completed: GlobalExceptionHandler (@RestControllerAdvice); handlers for JobNotFoundException (404), CompanyNotFoundException (404), MethodArgumentNotValidException (400 + ValidationErrorResponse), ObjectOptimisticLockingFailureException (409), Exception (500 + log).

## Next Steps

- PR #11: CompanyController (GET /api/companies, GET /{id}, POST, PATCH /{id}, DELETE /{id}).
- PR #12: JobController (GET /api/jobs, GET /{id}, POST, PATCH /{id}, DELETE /{id}; GET /{id} returns JobDetailDTO).

## Active Decisions

- Follow PRD and phase task files as source of truth.
- Version numbers and stack fixed in techContext (Java 17+, Spring Boot 3.x, PostgreSQL 15, etc.).
- No record of personal or motivational context in Memory Bank—only project and technical facts.
