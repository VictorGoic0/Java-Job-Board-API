# Progress: Job Board API

## What Works

- **PR #1 complete**: Spring Boot project (Java 17, Maven, Spring Boot 3.x); package structure `com.jobboard` (config, controller, service, repository, model/entity, model/dto, exception, util); Docker Compose for PostgreSQL 15; application.yaml (datasource, JPA, Jackson, server 8080, logging); .gitignore; app starts and connects to database.
- PRD and phase task files; Memory Bank; Cursor rule for PR completion and message sign-off.

## What's Left to Build

- **Phase I (remaining)**: PR #2 Company entity & repo → PR #3 Job entity & repo → PR #4–5 DTOs & mappers → PR #6–8 exceptions & global handler → PR #9–10 Company/Job services → PR #11–12 Company/Job controllers → full CRUD, validation, optimistic locking.
- **Phase II**: Search/filter, pagination, active jobs, deactivate.
- **Phase III**: Unit, repository, controller tests.
- **Phase IV**: Security (JWT), User, file upload, Application entity, scheduled expiration.

## Current Status

Phase I, PR #1 done. Ready for PR #2 (Company Entity & Repository).

## Known Issues

None.
