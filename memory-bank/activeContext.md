# Active Context: Job Board API

## Current Focus

PR #2 (Company Entity & Repository) and PR #3 (Job Entity & Repository with Enums) complete. Starting PR #4: DTOs for Company.

## Recent Changes

- PR #3 completed: Job-related enums (JobType, ExperienceLevel, RemoteOption), Job entity with all fields and indexes, JobRepository; schema verified (company and job tables created, FK, indexes, enum columns).
- PR #2 completed: Company entity, JpaConfig (@EnableJpaAuditing), CompanyRepository (Task 5 removed from PR #2; schema verification done after PR #3).

## Next Steps

- PR #4: Company DTOs (CompanySummaryDTO, CompanyDTO, CompanyCreateDTO, CompanyUpdateDTO), CompanyMapper.
- Then PR #5 (Job DTOs & salary validator), PR #6–8 (exceptions & handler), PR #9–12 (services & controllers).

## Active Decisions

- Follow PRD and phase task files as source of truth.
- Version numbers and stack fixed in techContext (Java 17+, Spring Boot 3.x, PostgreSQL 15, etc.).
- No record of personal or motivational context in Memory Bank—only project and technical facts.
