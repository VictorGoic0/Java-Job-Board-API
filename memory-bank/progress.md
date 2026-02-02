# Progress: Job Board API

## What Works

- **PR #1 complete**: Spring Boot project (Java 17, Maven, Spring Boot 3.x); package structure `com.jobboard`; Docker Compose for PostgreSQL 15; application.yaml; .gitignore; app starts and connects to database.
- **PR #2 complete**: Company entity, JpaConfig (@EnableJpaAuditing), CompanyRepository.
- **PR #3 complete**: Job enums (JobType, ExperienceLevel, RemoteOption), Job entity (all fields, indexes), JobRepository; schema verified (company and job tables, FK, indexes).
- **PR #4 complete**: CompanySummaryDTO, CompanyDTO, CompanyCreateDTO, CompanyUpdateDTO; CompanyMapper (toDTO, toSummaryDTO, toEntity, updateEntityFromDTO).
- **PR #5 complete**: JobDTO, JobDetailDTO (extends JobDTO), JobCreateDTO, JobUpdateDTO; ValidSalaryRange + SalaryRangeValidator (salaryMax >= salaryMin); JobMapper (toDTO, toDetailDTO, toEntity, updateEntityFromDTO; uses CompanyMapper).
- **PR #6 complete**: JobNotFoundException, CompanyNotFoundException, InvalidJobDataException, OptimisticLockException (all extend RuntimeException).
- **PR #7 complete**: ErrorResponse (message, status, timestamp), ValidationErrorResponse (extends ErrorResponse + Map<String, String> errors).
- **PR #8 complete**: GlobalExceptionHandler (@RestControllerAdvice); 404 for Job/Company not found, 400 + field errors for validation, 409 for optimistic lock, 500 + log for generic Exception.
- **PR #9 complete**: CompanyService (getAllCompanies, getCompanyById, createCompany, updateCompany, deleteCompany); CompanyRepository and CompanyMapper.
- **PR #10 complete**: JobService (getAllJobs via findAllWithCompany JOIN FETCH, getJobById → JobDetailDTO, createJob, updateJob, deleteJob); company validation on create/update; JobRepository.findAllWithCompany().
- PRD and phase task files; Memory Bank; Cursor rule for PR completion and Vader quote sign-off.

## What's Left to Build

- **Phase I (remaining)**: PR #11 CompanyController → PR #12 JobController → full CRUD, validation, optimistic locking.
- **Phase II**: Search/filter, pagination, active jobs, deactivate.
- **Phase III**: Unit, repository, controller tests.
- **Phase IV**: Security (JWT), User, file upload, Application entity, scheduled expiration.

## Current Status

Phase I, PR #10 done. Ready for PR #11 (Company Controller).

## Known Issues

None.
