# Progress: Job Board API

## What Works

- **PR #1 complete**: Spring Boot project (Java 17, Maven, Spring Boot 3.x); package structure `com.jobboard`; Docker Compose for PostgreSQL 15; application.yaml; .gitignore; app starts and connects to database.
- **PR #2 complete**: Company entity, JpaConfig (@EnableJpaAuditing), CompanyRepository.
- **PR #3 complete**: Job-related enums (JobType, ExperienceLevel, RemoteOption), Job entity (all fields, indexes on company_id, is_active, posted_date), JobRepository; schema verified (company and job tables, FK, indexes, enum columns).
- PRD and phase task files; Memory Bank; Cursor rule for PR completion and message sign-off.

## What's Left to Build

- **Phase I (remaining)**: PR #4–5 DTOs & mappers (Company, Job; salary validator) → PR #6–8 exceptions & global handler → PR #9–10 Company/Job services → PR #11–12 Company/Job controllers → full CRUD, validation, optimistic locking.
- **Phase II**: Search/filter, pagination, active jobs, deactivate.
- **Phase III**: Unit, repository, controller tests.
- **Phase IV**: Security (JWT), User, file upload, Application entity, scheduled expiration.

## Current Status

Phase I, PR #3 done. Ready for PR #4 (DTOs for Company).

## Known Issues

None.
