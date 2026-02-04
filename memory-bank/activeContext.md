# Active Context: Job Board API

## Current Focus

PR #14 complete (Active Jobs Repository Query). Next: PR #15 (Search/Filter Repository Query).

## Recent Changes

- PR #14 completed: JobRepository.findActiveJobs(Pageable) with @Query JPQL (isActive = true AND (expiryDate IS NULL OR expiryDate > CURRENT_TIMESTAMP)), countQuery for pagination, JOIN FETCH company. JobService.getActiveJobs(Pageable); JobController GET /api/jobs/active with same pagination params (page, size, sort). Manual verification tasks marked complete in phase_2_tasks.md.
- PR #13 completed: Pagination on GET /api/jobs and GET /api/companies.
- Phase I fully complete (all 12 PRs).

## Next Steps

- PR #15: Add searchJobs repository query (keyword, location, companyId, enums, salary, isActive).
- Then: search endpoint, deactivate endpoint (active jobs endpoint delivered via GET /api/jobs/active in PR #14).

## Active Decisions

- Follow PRD and phase task files as source of truth.
- Version numbers and stack fixed in techContext (Java 17+, Spring Boot 3.x, PostgreSQL 15, etc.).
- No record of personal or motivational context in Memory Bank—only project and technical facts.
