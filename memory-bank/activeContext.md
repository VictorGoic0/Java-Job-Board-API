# Active Context: Job Board API

## Current Focus

PR #13 complete (pagination on GET /api/jobs and GET /api/companies). Next: PR #14 (Active Jobs Repository Query).

## Recent Changes

- PR #13 completed: Pagination on existing list endpoints. JobService.getAllJobs(Pageable) → Page<JobDTO>; JobRepository.findAllWithCompany(Pageable); JobController.getAllJobs(page, size, sort) with @Min(0) page, @Min(1) @Max(100) size, parseSort with whitelist (ALLOWED_JOB_SORT_FIELDS). CompanyService.getAllCompanies(Pageable) → Page<CompanyDTO>; CompanyController.getAllCompanies(page, size, sort) with same validation and parseSort (ALLOWED_COMPANY_SORT_FIELDS). Manual pagination tests marked complete.
- Phase I fully complete (all 12 PRs and manual testing / completion checklist done).
- PR #12 completed: JobController; PR #11 CompanyController; PR #10 JobService; PR #9 CompanyService.

## Next Steps

- PR #14: Add findActiveJobs(Pageable) to JobRepository (JPQL: isActive = true AND (expiryDate IS NULL OR expiryDate > CURRENT_TIMESTAMP)); manual verification.
- PR #15: Add searchJobs repository query (keyword, location, companyId, enums, salary, isActive).
- Then: active jobs endpoint, search endpoint, deactivate endpoint.

## Active Decisions

- Follow PRD and phase task files as source of truth.
- Version numbers and stack fixed in techContext (Java 17+, Spring Boot 3.x, PostgreSQL 15, etc.).
- No record of personal or motivational context in Memory Bank—only project and technical facts.
