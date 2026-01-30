# Tech Context: Job Board API

## Tech Stack (with versions)

| Component | Choice | Version / notes |
|-----------|--------|-----------------|
| Language | Java | 17+ |
| Framework | Spring Boot | 3.x |
| Build | Maven | — |
| Database | PostgreSQL | 15 (image: postgres:15-alpine) |
| ORM | Spring Data JPA / Hibernate | Via Spring Boot 3.x |
| Containerization | Docker, Docker Compose | — |
| Testing | JUnit 5, Mockito, MockMvc | Via spring-boot-starter-test |
| Test DB | H2 | In-memory for repository/integration tests (Phase III) |
| Phase IV auth | Spring Security, JWT | jjwt 0.11.5 (api, impl, jackson) |
| Validation | Bean Validation | Via Spring Boot (validation starter) |
| Lombok | Optional | To reduce boilerplate (per PRD) |

## Development Setup

- **Runtime**: Java 17+, Maven.
- **Database**: Docker Compose starts PostgreSQL 15 (container name `job_board_db`, port 5432, db `job_board`, user/pass per PRD or env).
- **App config**: `application.yml` / `application.properties`; JPA `ddl-auto: update` for dev; datasource URL `jdbc:postgresql://localhost:5432/job_board`.
- **Test config**: `application-test.yml`; H2 in-memory; `ddl-auto: create-drop` for tests.
- **Server**: Default port 8080 (configurable).

## Dependencies (summary)

- Spring Web, Spring Data JPA, PostgreSQL driver, Validation, Lombok (Phase I).
- H2 (test scope) for Phase III.
- Spring Security + jjwt 0.11.5 for Phase IV.

## Technical Constraints

- Java 17+ and Spring Boot 3.x dictate baseline (e.g. Jakarta namespace, not javax).
- PostgreSQL dialect and types (e.g. DECIMAL, TIMESTAMP) used in schema and JPA.
- Maven as single build tool; no Gradle in scope.
- Docker Compose for local Postgres; deployment Dockerfile in PRD is OpenJDK 17 slim.
