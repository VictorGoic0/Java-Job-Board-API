# Product Context: Job Board API

## What It Is

A job board platform backend: REST API built with Spring Boot and Java. Clients can manage companies and job postings, search and filter jobs, and (in Phase IV) authenticate, upload resumes, and submit applications.

## Problems It Solves

- Centralized job and company data with consistent validation and error handling.
- Concurrent updates handled safely via optimistic locking.
- Search and filter over jobs (keyword, location, company, type, level, remote, salary, active).
- Pagination to avoid large payloads and memory issues.
- (Phase IV) Secured write operations and application workflow with roles and file storage.

## How It Works

- **Companies**: CRUD; each company has name, description, website, location; optional list of jobs.
- **Jobs**: CRUD; each job belongs to one company; has title, description, location, salary range, job type, experience level, remote option, posted/expiry dates, active flag, application URL. Enums: JobType (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP), ExperienceLevel (ENTRY, MID, SENIOR), RemoteOption (REMOTE, HYBRID, ONSITE).
- **Search**: `GET /api/jobs/search` with optional query params; paginated response.
- **Active jobs**: `GET /api/jobs/active` — active and non-expired only.
- **Deactivate**: Soft delete via `POST /api/jobs/{id}/deactivate`.
- **Errors**: Global handler returns structured bodies (message, status, timestamp; validation errors include field-level details).

## Out of Scope (per PRD)

- No frontend; API only.
- No external job feeds or integrations specified.
- Deployment/platform details are considerations only; implementation is project-specific.
