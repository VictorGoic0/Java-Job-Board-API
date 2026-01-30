# Project Brief: Job Board API

## Purpose

Production-ready RESTful API for a job board platform. Manages companies and job postings with full CRUD, advanced search, validation, and concurrency control (optimistic locking).

## Scope

- **Phase I**: Core setup, PostgreSQL (Docker), JPA entities (Company, Job), full CRUD for Companies and Jobs, DTOs, validation, global exception handling, optimistic locking.
- **Phase II**: Search/filter endpoint (`GET /api/jobs/search`), pagination, active-jobs route, soft delete (deactivate).
- **Phase III**: Unit tests (services), repository tests (H2), controller tests (MockMvc).
- **Phase IV**: Spring Security, JWT auth, User/roles, resume file upload, Application entity, scheduled job auto-expire.

## Core Requirements

- REST API under `/api/` (companies, jobs; later auth, applications).
- Database: Company → Jobs (one-to-many); referential integrity; cascade delete company → jobs.
- Concurrency: optimistic locking via `@Version` on Company and Job; 409 on conflict.
- Validation and structured error responses (400/404/409/500).
- Layered architecture: Controller → Service → Repository; DTOs for API boundaries.

## Source of Truth

- **PRD.md**: Full product and technical specification.
- **phase_1_tasks.md** through **phase_4_part_2_tasks.md**: Phase-specific task breakdowns.
