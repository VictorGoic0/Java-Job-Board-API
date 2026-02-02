# Active Context: Job Board API

## Current Focus

PR #4 through PR #8 complete. Next: PR #9 (Company Service Layer).

## Recent Changes

- PR #8 completed: GlobalExceptionHandler (@RestControllerAdvice); handlers for JobNotFoundException (404), CompanyNotFoundException (404), MethodArgumentNotValidException (400 + ValidationErrorResponse), ObjectOptimisticLockingFailureException (409), Exception (500 + log).
- PR #7 completed: ErrorResponse and ValidationErrorResponse DTOs (model/dto).
- PR #6 completed: JobNotFoundException, CompanyNotFoundException, InvalidJobDataException, OptimisticLockException (exception/).
- PR #5 completed: JobDTO, JobDetailDTO, JobCreateDTO, JobUpdateDTO; ValidSalaryRange + SalaryRangeValidator (validation/); JobMapper (util/) with CompanyMapper for embedded company.
- PR #4 completed: CompanySummaryDTO, CompanyDTO, CompanyCreateDTO, CompanyUpdateDTO; CompanyMapper (util/).

## Next Steps

- PR #9: CompanyService (@Service, @Transactional), getAllCompanies, getCompanyById, createCompany, updateCompany (partial via mapper), deleteCompany; inject CompanyRepository and CompanyMapper.
- PR #10: JobService (same pattern; avoid N+1 for company data; validate company exists on create/update).
- PR #11: CompanyController (GET all, GET /{id}, POST, PATCH /{id}, DELETE /{id}).
- PR #12: JobController (same; GET /api/jobs with embedded company, GET /{id} returns JobDetailDTO).

## Active Decisions

- Follow PRD and phase task files as source of truth.
- Version numbers and stack fixed in techContext (Java 17+, Spring Boot 3.x, PostgreSQL 15, etc.).
- No record of personal or motivational context in Memory Bank—only project and technical facts.
