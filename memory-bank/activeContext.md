# Active Context: Job Board API

## Current Focus

PR #1 (Project Setup & Configuration) complete. Starting PR #2: Company Entity & Repository.

## Recent Changes

- PR #1 completed: Spring Boot project initialized (Java 17, Maven, Spring Boot 3.x, YAML), package structure under `com.jobboard`, Docker Compose for PostgreSQL 15, application.yaml (datasource, JPA, Jackson, server port, logging), .gitignore updated, app starts and connects to DB.
- Rule added: update Memory Bank when a PR is fully complete; update systemPatterns when significant system-pattern changes land in code.

## Next Steps

- PR #2: Company entity, JpaConfig (@EnableJpaAuditing), CompanyRepository, verify company table generation.
- Then PR #3 (Job entity & enums), PR #4–5 (DTOs), PR #6–8 (exceptions & handler), PR #9–12 (services & controllers).

## Active Decisions

- Follow PRD and phase task files as source of truth.
- Version numbers and stack fixed in techContext (Java 17+, Spring Boot 3.x, PostgreSQL 15, etc.).
- No record of personal or motivational context in Memory Bank—only project and technical facts.
