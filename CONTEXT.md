# Pick-up Context: Job Board API

Use this file to resume work in a new tab. It summarizes recent work and where the project stands.

---

## Project at a Glance

- **What it is**: REST API for a job board (companies and jobs), built with **Spring Boot 3.x**, **Java 17**, **Maven**, **PostgreSQL 15** (Docker Compose).
- **Source of truth**: `PRD.md` and `phase_1_tasks.md` (Phase I task list). Memory Bank in `memory-bank/` (activeContext, progress, techContext, etc.).
- **Package root**: `com.jobboard` under `src/main/java/com/jobboard/`.

---

## Current State (Where We Left Off)

**Phase I is in progress.** PRs #1–#8 are **complete**. Next up is **PR #9: Company Service Layer**, then PR #10 (Job Service), PR #11 (Company Controller), PR #12 (Job Controller).

### Completed (PR #1–#8)

| PR     | Delivered                                                                                                                                                                                                           |
| ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **#1** | Project setup: Spring Boot, Maven, Docker Compose (Postgres 15), application.yaml, .gitignore, folder structure.                                                                                                    |
| **#2** | Company entity, JpaConfig (@EnableJpaAuditing), CompanyRepository. Task 5 (schema test) removed from PR #2; schema verified after PR #3.                                                                            |
| **#3** | Job enums (JobType, ExperienceLevel, RemoteOption), Job entity (all fields, indexes on company_id, is_active, posted_date), JobRepository. Schema verified.                                                         |
| **#4** | Company DTOs: CompanySummaryDTO, CompanyDTO, CompanyCreateDTO, CompanyUpdateDTO; CompanyMapper (toDTO, toSummaryDTO, toEntity, updateEntityFromDTO).                                                                |
| **#5** | Job DTOs: JobDTO, JobDetailDTO (extends JobDTO), JobCreateDTO, JobUpdateDTO; ValidSalaryRange + SalaryRangeValidator (validation/); JobMapper (uses CompanyMapper for embedded company).                            |
| **#6** | Custom exceptions: JobNotFoundException, CompanyNotFoundException, InvalidJobDataException, OptimisticLockException (exception/).                                                                                   |
| **#7** | ErrorResponse and ValidationErrorResponse (model/dto); used by exception handler.                                                                                                                                   |
| **#8** | GlobalExceptionHandler (@RestControllerAdvice): 404 (Job/Company not found), 400 (MethodArgumentNotValidException → ValidationErrorResponse), 409 (ObjectOptimisticLockingFailureException), 500 (Exception + log). |

### Next: PR #9 (Company Service Layer)

- Create `service/CompanyService.java`: @Service, @Transactional.
- Inject CompanyRepository and CompanyMapper (constructor).
- Methods: getAllCompanies (readOnly), getCompanyById (readOnly, throw CompanyNotFoundException), createCompany, updateCompany (partial via mapper), deleteCompany (cascade deletes jobs).
- See `phase_1_tasks.md` section "PR #9: Company Service Layer" for full checklist.

### After That

- **PR #10**: JobService (getAllJobs with N+1 avoided, getJobById → JobDetailDTO, create/update/delete; validate company exists).
- **PR #11**: CompanyController — GET /api/companies, GET /api/companies/{id}, POST, PATCH /{id}, DELETE /{id}.
- **PR #12**: JobController — GET /api/jobs, GET /api/jobs/{id}, POST, PATCH /{id}, DELETE /{id}.

---

## Key Paths and Conventions

- **Entities**: `src/main/java/com/jobboard/model/entity/` (Company, Job, enums).
- **DTOs**: `src/main/java/com/jobboard/model/dto/`.
- **Repositories**: `src/main/java/com/jobboard/repository/` (CompanyRepository, JobRepository).
- **Mappers**: `src/main/java/com/jobboard/util/` (CompanyMapper, JobMapper).
- **Validation**: `src/main/java/com/jobboard/validation/` (ValidSalaryRange, SalaryRangeValidator).
- **Exceptions**: `src/main/java/com/jobboard/exception/` (custom exceptions + GlobalExceptionHandler).
- **Config**: `src/main/java/com/jobboard/config/` (JpaConfig).
- **App config**: `src/main/resources/application.yaml` (datasource, JPA, server 8080).
- **Phase I tasks**: `phase_1_tasks.md`; checkboxes updated as tasks complete.

---

## How to Run

1. **Database**: `docker-compose up -d` (Postgres 15 on port 5432, db `job_board`, user/pass per docker-compose.yml).
2. **App**: `./mvnw spring-boot:run` from project root (Java 17, port 8080).
3. **Build**: `./mvnw compile` (or `package`).

---

## Cursor / Project Rules

- **Memory Bank**: Update `memory-bank/activeContext.md` and `memory-bank/progress.md` when a **PR is fully complete** (last task checked). Update `memory-bank/systemPatterns.md` when significant system-pattern changes land.
- **Message sign-off**: End every reply with a Darth Vader quote (see `.cursor/rules/project-conventions.mdc`; .cursor is in .gitignore).

---

## Tech Stack (Fixed)

- Java 17+, Spring Boot 3.x, Maven, PostgreSQL 15 (postgres:15-alpine), Spring Data JPA / Hibernate, Lombok, Bean Validation. Testing (Phase III): JUnit 5, Mockito, MockMvc, H2.

---

_Last updated: After completing PR #8 (Global Exception Handler). Next: PR #9 Company Service._
