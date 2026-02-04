# Pick-up Context: Job Board API

Use this file to resume work in a new tab. It summarizes recent work and where the project stands.

---

## Project at a Glance

- **What it is**: REST API for a job board (companies and jobs), built with **Spring Boot 3.x**, **Java 17**, **Maven**, **PostgreSQL 15** (Docker Compose).
- **Source of truth**: `PRD.md`, `phase_1_tasks.md`, `phase_2_tasks.md`. Memory Bank in `memory-bank/` (activeContext, progress, techContext, systemPatterns).
- **Package root**: `com.jobboard` under `src/main/java/com/jobboard/`.

---

## Current State (Where We Left Off)

**Phase II is in progress.** PR #14 (Active Jobs Repository Query) is **complete**. Next up is **PR #15: Add Search/Filter Repository Query**, then search endpoint, deactivate endpoint.

### Recently Completed: PR #14 (Active Jobs Repository Query)

- **JobRepository**: `Page<Job> findActiveJobs(Pageable pageable)` with `@Query` JPQL: `j.isActive = true AND (j.expiryDate IS NULL OR j.expiryDate > CURRENT_TIMESTAMP)`, JOIN FETCH company, countQuery for pagination.
- **JobService**: `getActiveJobs(Pageable pageable)` → `Page<JobDTO>`; uses `jobRepository.findActiveJobs(pageable)`.
- **JobController**: GET /api/jobs/active with same pagination params as GET /api/jobs (page, size, sort). Returns only active, non-expired jobs.
- PR #14 tasks marked complete in phase_2_tasks.md.

### Next: PR #15 (Search/Filter Repository Query)

- Add **searchJobs** repository method (keyword, location, companyId, enums, salary range, isActive; paginated).
- See `phase_2_tasks.md` section "PR #15: Add Search/Filter Repository Query" for full checklist.

### After That

- Search endpoint (GET /api/jobs/search), deactivate endpoint (POST /api/jobs/{id}/deactivate).

---

## Key Paths and Conventions

- **Entities**: `src/main/java/com/jobboard/model/entity/` (Company, Job, enums).
- **DTOs**: `src/main/java/com/jobboard/model/dto/`.
- **Repositories**: `src/main/java/com/jobboard/repository/` (CompanyRepository, JobRepository).
- **Services**: `src/main/java/com/jobboard/service/` (CompanyService, JobService).
- **Controllers**: `src/main/java/com/jobboard/controller/` (CompanyController, JobController).
- **Mappers**: `src/main/java/com/jobboard/util/` (CompanyMapper, JobMapper).
- **Validation**: `src/main/java/com/jobboard/validation/` (ValidSalaryRange, SalaryRangeValidator).
- **Exceptions**: `src/main/java/com/jobboard/exception/` (custom exceptions + GlobalExceptionHandler).
- **Config**: `src/main/java/com/jobboard/config/` (JpaConfig).
- **App config**: `src/main/resources/application.yaml` (datasource, JPA, server 8080).
- **Phase I tasks**: `phase_1_tasks.md`; **Phase II tasks**: `phase_2_tasks.md`.

---

## How to Run

1. **Database**: `docker-compose up -d` (Postgres 15 on port 5432, db `job_board`, user/pass per docker-compose.yml).
2. **App**: `./mvnw spring-boot:run` from project root (Java 17, port 8080).
3. **Build**: `./mvnw compile` (or `package`).

---

## Cursor / Project Rules

- **Memory Bank**: Update `memory-bank/activeContext.md` and `memory-bank/progress.md` when a **PR is fully complete** (last task checked). Update `memory-bank/systemPatterns.md` when significant system-pattern changes land.
- **Message sign-off**: End every reply with a Darth Vader quote (see `.cursor/rules/project-conventions.mdc`).

---

## Tech Stack (Fixed)

- Java 17+, Spring Boot 3.x, Maven, PostgreSQL 15 (postgres:15-alpine), Spring Data JPA / Hibernate, Lombok, Bean Validation. Testing (Phase III): JUnit 5, Mockito, MockMvc, H2.

---

_Last updated: After completing PR #14 (Active Jobs Repository Query). Next: PR #15 (Search/Filter Repository Query)._
