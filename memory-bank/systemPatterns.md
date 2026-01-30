# System Patterns: Job Board API

## Architecture

- **Layered**: Controller → Service → Repository. Entities and DTOs are separate; API boundaries use DTOs only.
- **REST**: JSON over HTTP; standard verbs and status codes (200, 201, 204, 400, 404, 409, 500).
- **Stateless**: No server-side session; Phase IV uses JWT for auth.

## Key Technical Decisions

- **Optimistic locking**: `@Version` (Integer) on Company and Job. Concurrent update → `ObjectOptimisticLockingFailureException` → 409 and clear message.
- **Validation**: Bean Validation (`@Valid`, `@NotBlank`, `@NotNull`, `@Pattern`, `@DecimalMin`, `@Future`, etc.) on DTOs and entities; `MethodArgumentNotValidException` mapped to 400 with field errors.
- **PATCH semantics**: Update DTOs have optional fields; only non-null fields are applied (partial update).
- **Soft delete**: Deactivate job sets `is_active = false`; no row delete for that path.
- **N+1 avoidance**: List endpoints use join fetch (e.g. job + company) where needed; repositories expose appropriate queries.

## Component Relationships

- **Controller**: Receives HTTP, validates input via `@Valid`, delegates to Service, returns ResponseEntity and DTOs.
- **Service**: `@Transactional`; business rules, entity ↔ DTO mapping, calls Repository; throws domain exceptions (e.g. JobNotFoundException, CompanyNotFoundException, OptimisticLockException).
- **Repository**: Spring Data JPA `JpaRepository`; custom methods for search, active jobs, findByCompanyId, etc.
- **GlobalExceptionHandler**: `@RestControllerAdvice`; maps exceptions to ErrorResponse / ValidationErrorResponse and HTTP status.

## Design Conventions

- DTOs: Create/Update/Summary/Detail variants as needed (e.g. JobCreateDTO, JobUpdateDTO, JobDTO, JobDetailDTO, CompanySummaryDTO).
- Enums stored as STRING in DB.
- Timestamps: `created_at` / `updated_at` via `@CreatedDate` / `@LastModifiedDate` (auditing).
- IDs: Long, identity-generated.
