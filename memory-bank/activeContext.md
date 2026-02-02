# Active Context: Job Board API

## Current Focus

Phase I complete (PR #1–#12). Next: Phase II (search, pagination, active jobs, deactivate) or Phase III (testing).

## Recent Changes

- PR #12 completed: JobController (@RestController, /api/jobs); GET all (List<JobDTO>), GET /{id} (JobDetailDTO), POST (201 + JobDTO), PATCH /{id}, DELETE /{id} (204).
- PR #11 completed: CompanyController (@RestController, /api/companies); GET all, GET /{id}, POST (201), PATCH /{id}, DELETE /{id} (204).
- PR #10 completed: JobService; getAllJobs (findAllWithCompany JOIN FETCH), getJobById (JobDetailDTO), createJob, updateJob, deleteJob; company validation; JobRepository.findAllWithCompany().
- PR #9 completed: CompanyService; getAllCompanies, getCompanyById, createCompany, updateCompany, deleteCompany.

## Next Steps

- Phase II: Search/filter endpoint, pagination, GET /api/jobs/active, POST /api/jobs/{id}/deactivate.
- Phase III: Unit tests (services), repository tests, controller tests.

## Active Decisions

- Follow PRD and phase task files as source of truth.
- Version numbers and stack fixed in techContext (Java 17+, Spring Boot 3.x, PostgreSQL 15, etc.).
- No record of personal or motivational context in Memory Bank—only project and technical facts.
