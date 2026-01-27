# Phase IV Part 1: Authentication & Authorization - Implementation Tasks

**Reference**: See PRD Section "Phase IV: Advanced Features" - Deliverable 1

**Overview**: Implement JWT-based authentication system with role-based access control. Users can register, login, and access endpoints based on their role (ADMIN, RECRUITER, JOB_SEEKER).

---

## PR #31: Add Security Dependencies & Basic Configuration

**PRD Reference**: Phase IV, Deliverable 1 - Authentication & Authorization (Dependencies)

### Tasks

- [ ] 1. Add Spring Security dependencies to pom.xml
  - [ ] Add spring-boot-starter-security
  - [ ] Add jjwt-api (version 0.11.5)
  - [ ] Add jjwt-impl (version 0.11.5, runtime scope)
  - [ ] Add jjwt-jackson (version 0.11.5, runtime scope)

- [ ] 2. Run `mvn clean install` to download dependencies
  - [ ] Verify build succeeds
  - [ ] Verify dependencies downloaded

- [ ] 3. Create security package structure
  ```
  src/main/java/com/jobboard/security/
    ├── JwtTokenProvider.java
    ├── JwtAuthenticationFilter.java
    ├── SecurityConfig.java
    └── CustomUserDetailsService.java
  ```

- [ ] 4. Add JWT configuration properties
  - [ ] Open application.yml
  - [ ] Add jwt.secret property (min 256 bits)
  - [ ] Add jwt.expiration property (milliseconds, e.g., 86400000 for 24 hours)

- [ ] 5. Test application still starts
  - [ ] Run application
  - [ ] Note: Spring Security will auto-configure and block all endpoints
  - [ ] This is expected - we'll configure access in next PRs

**Acceptance Criteria**:
- Security dependencies added
- JWT properties configured
- Security package structure created
- Application starts (all endpoints now return 401)

---

## PR #32: User Entity & Repository

**PRD Reference**: Phase IV, Deliverable 1 - User Entity

### Tasks

- [ ] 1. Create UserRole enum
  - [ ] Create `model/entity/UserRole.java`
  - [ ] Add values: ADMIN, RECRUITER, JOB_SEEKER
  - [ ] Note: These map to Spring Security roles (will have ROLE_ prefix)

- [ ] 2. Create User entity
  - [ ] Create `model/entity/User.java`
  - [ ] Add `@Entity` and `@Table(name = "users")`
  - [ ] Add id with `@Id` and `@GeneratedValue`
  - [ ] Add email field with `@NotBlank`, `@Email`, `@Column(unique = true, nullable = false)`
  - [ ] Add password field with `@NotBlank`, `@Column(nullable = false)` (will store BCrypt hash)
  - [ ] Add fullName field with `@NotBlank`
  - [ ] Add role field with `@Enumerated(STRING)`, `@Column(nullable = false)`
  - [ ] Add enabled field with `@Column(nullable = false)`, default true
  - [ ] Add createdAt, updatedAt with JPA auditing
  - [ ] Add `@Version` for optimistic locking
  - [ ] Generate getters/setters with Lombok

- [ ] 3. Create UserRepository interface
  - [ ] Create `repository/UserRepository.java`
  - [ ] Extend `JpaRepository<User, Long>`
  - [ ] Add method: `Optional<User> findByEmail(String email)`
  - [ ] Add method: `Boolean existsByEmail(String email)`

- [ ] 4. Test database schema generation
  - [ ] Start application
  - [ ] Verify `users` table created
  - [ ] Verify email has unique constraint
  - [ ] Verify columns match entity definition

**Acceptance Criteria**:
- UserRole enum created
- User entity created with all fields
- UserRepository created
- Database table generated correctly
- Email uniqueness enforced at database level

---

## PR #33: JWT Token Provider

**PRD Reference**: Phase IV, Deliverable 1 - JWT Implementation

### Tasks

- [ ] 1. Create JwtTokenProvider class
  - [ ] Create `security/JwtTokenProvider.java`
  - [ ] Add `@Component` annotation
  - [ ] Inject jwt.secret and jwt.expiration from properties using `@Value`

- [ ] 2. Implement generateToken method
  - [ ] Method signature: `String generateToken(Authentication authentication)`
  - [ ] Get UserDetails from authentication principal
  - [ ] Use Jwts.builder() to create token
  - [ ] Set subject to username (email)
  - [ ] Set issued date to now
  - [ ] Set expiration date using configured expiration time
  - [ ] Sign with HMAC SHA512 and secret key
  - [ ] Return compact JWT string

- [ ] 3. Implement getUsernameFromToken method
  - [ ] Method signature: `String getUsernameFromToken(String token)`
  - [ ] Parse JWT token
  - [ ] Extract and return subject (username/email)

- [ ] 4. Implement validateToken method
  - [ ] Method signature: `boolean validateToken(String token)`
  - [ ] Try to parse token using secret key
  - [ ] Catch ExpiredJwtException - return false
  - [ ] Catch MalformedJwtException - return false
  - [ ] Catch SignatureException - return false
  - [ ] Catch IllegalArgumentException - return false
  - [ ] If no exception, return true

- [ ] 5. Add helper method to extract claims
  - [ ] Private method to parse token and get claims
  - [ ] Reuse in getUsernameFromToken and validateToken

- [ ] 6. Add logging
  - [ ] Add SLF4J logger
  - [ ] Log JWT validation failures for debugging
  - [ ] Don't log full tokens (security risk)

**Acceptance Criteria**:
- JwtTokenProvider class created
- Token generation works
- Token validation works
- Username extraction works
- Expired tokens rejected
- Invalid tokens rejected
- Proper exception handling

---

## PR #34: Custom UserDetailsService

**PRD Reference**: Phase IV, Deliverable 1 - Spring Security Integration

### Tasks

- [ ] 1. Create CustomUserDetailsService class
  - [ ] Create `security/CustomUserDetailsService.java`
  - [ ] Add `@Service` annotation
  - [ ] Implement `UserDetailsService` interface
  - [ ] Inject UserRepository

- [ ] 2. Implement loadUserByUsername method
  - [ ] Method signature: `UserDetails loadUserByUsername(String email)`
  - [ ] Fetch user by email from repository
  - [ ] Throw UsernameNotFoundException if not found
  - [ ] Build Spring Security UserDetails from User entity
  - [ ] Set username to user's email
  - [ ] Set password to user's password (already hashed)
  - [ ] Set authorities from user's role (prepend "ROLE_")
  - [ ] Set account enabled from user's enabled field
  - [ ] Return UserDetails object

- [ ] 3. Create UserPrincipal class (optional, alternative approach)
  - [ ] Can create custom class implementing UserDetails
  - [ ] Or use User.builder() from Spring Security
  - [ ] Decision: Use Spring's User.builder() for simplicity

**Acceptance Criteria**:
- CustomUserDetailsService created
- Implements UserDetailsService interface
- Loads user by email
- Converts User entity to UserDetails
- Handles user not found case
- Sets authorities correctly with ROLE_ prefix

---

## PR #35: JWT Authentication Filter

**PRD Reference**: Phase IV, Deliverable 1 - JWT Filter

### Tasks

- [ ] 1. Create JwtAuthenticationFilter class
  - [ ] Create `security/JwtAuthenticationFilter.java`
  - [ ] Extend `OncePerRequestFilter`
  - [ ] Add `@Component` annotation
  - [ ] Inject JwtTokenProvider
  - [ ] Inject CustomUserDetailsService

- [ ] 2. Implement doFilterInternal method
  - [ ] Extract JWT token from Authorization header
  - [ ] Check if header starts with "Bearer "
  - [ ] If yes, extract token (remove "Bearer " prefix)
  - [ ] Validate token using JwtTokenProvider
  - [ ] If valid, extract username from token
  - [ ] Load UserDetails using CustomUserDetailsService
  - [ ] Create Authentication object (UsernamePasswordAuthenticationToken)
  - [ ] Set authentication in SecurityContext
  - [ ] Continue filter chain

- [ ] 3. Add error handling
  - [ ] Catch exceptions during token processing
  - [ ] Log errors
  - [ ] Don't set authentication if token invalid
  - [ ] Allow request to proceed (SecurityConfig will handle unauthorized)

- [ ] 4. Handle missing token
  - [ ] If no Authorization header or no Bearer token
  - [ ] Simply continue filter chain without setting authentication
  - [ ] Public endpoints will still be accessible

**Acceptance Criteria**:
- JwtAuthenticationFilter created
- Extends OncePerRequestFilter
- Extracts token from Authorization header
- Validates token
- Sets authentication in SecurityContext
- Handles errors gracefully
- Allows filter chain to continue

---

## PR #36: Security Configuration

**PRD Reference**: Phase IV, Deliverable 1 - Security Configuration

### Tasks

- [ ] 1. Create SecurityConfig class
  - [ ] Create `security/SecurityConfig.java`
  - [ ] Add `@Configuration` annotation
  - [ ] Add `@EnableWebSecurity` annotation
  - [ ] Add `@EnableMethodSecurity` annotation
  - [ ] Inject JwtAuthenticationFilter
  - [ ] Inject CustomUserDetailsService

- [ ] 2. Configure PasswordEncoder bean
  - [ ] Add `@Bean` method returning PasswordEncoder
  - [ ] Return `new BCryptPasswordEncoder()`
  - [ ] This will be used for password hashing

- [ ] 3. Configure AuthenticationManager bean
  - [ ] Add `@Bean` method for AuthenticationManager
  - [ ] Use AuthenticationManagerBuilder
  - [ ] Set UserDetailsService
  - [ ] Set PasswordEncoder
  - [ ] Return built AuthenticationManager

- [ ] 4. Configure SecurityFilterChain bean
  - [ ] Add `@Bean` method for SecurityFilterChain
  - [ ] Disable CSRF (since using JWT)
  - [ ] Configure authorizeHttpRequests:
    - [ ] Permit /api/auth/** (registration, login)
    - [ ] Permit GET /api/jobs/** (view jobs)
    - [ ] Permit GET /api/companies/** (view companies)
    - [ ] Require ADMIN or RECRUITER for POST/PUT/PATCH/DELETE /api/jobs/**
    - [ ] Require ADMIN or RECRUITER for /api/companies/**
    - [ ] Require authentication for /api/applications/**
    - [ ] Authenticate all other requests
  - [ ] Configure session management to STATELESS
  - [ ] Add JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
  - [ ] Return built SecurityFilterChain

- [ ] 5. Configure CORS (if needed for frontend)
  - [ ] Add CORS configuration
  - [ ] Allow appropriate origins
  - [ ] Allow appropriate methods and headers
  - [ ] Note: Can skip if no frontend yet

**Acceptance Criteria**:
- SecurityConfig class created
- PasswordEncoder bean configured (BCrypt)
- AuthenticationManager bean configured
- SecurityFilterChain configured with proper rules:
  - Auth endpoints public
  - GET endpoints for jobs/companies public
  - Create/update/delete requires RECRUITER or ADMIN
  - Applications require authentication
- JWT filter added to chain
- Session management stateless
- Application starts successfully

---

## PR #37: Auth DTOs

**PRD Reference**: Phase IV, Deliverable 1 - Auth Endpoints (DTOs)

### Tasks

- [ ] 1. Create RegisterRequest DTO
  - [ ] Create `model/dto/auth/RegisterRequest.java`
  - [ ] Add fields: email, password, fullName, role
  - [ ] Add validation: `@NotBlank` on email, password, fullName
  - [ ] Add `@Email` on email
  - [ ] Add `@NotNull` on role
  - [ ] Add `@Size(min = 8)` on password
  - [ ] Use Lombok @Data

- [ ] 2. Create LoginRequest DTO
  - [ ] Create `model/dto/auth/LoginRequest.java`
  - [ ] Add fields: email, password
  - [ ] Add validation: `@NotBlank @Email` on email
  - [ ] Add `@NotBlank` on password
  - [ ] Use Lombok @Data

- [ ] 3. Create AuthResponse DTO
  - [ ] Create `model/dto/auth/AuthResponse.java`
  - [ ] Add fields: token (String), type (String, default "Bearer"), email, fullName, role
  - [ ] Use Lombok @Data
  - [ ] Add all-args constructor

- [ ] 4. Create UserDTO (for "me" endpoint)
  - [ ] Create `model/dto/auth/UserDTO.java`
  - [ ] Add fields: id, email, fullName, role, enabled, createdAt
  - [ ] Use Lombok @Data

**Acceptance Criteria**:
- All auth DTOs created
- Validation annotations applied
- DTOs ready for use in controller
- Password field has minimum length validation

---

## PR #38: Auth Service

**PRD Reference**: Phase IV, Deliverable 1 - Auth Endpoints (Service Layer)

### Tasks

- [ ] 1. Create AuthService class
  - [ ] Create `service/AuthService.java`
  - [ ] Add `@Service` annotation
  - [ ] Inject UserRepository
  - [ ] Inject PasswordEncoder
  - [ ] Inject AuthenticationManager
  - [ ] Inject JwtTokenProvider

- [ ] 2. Implement register method
  - [ ] Method signature: `AuthResponse register(RegisterRequest request)`
  - [ ] Check if email already exists using repository.existsByEmail()
  - [ ] If exists, throw exception (create EmailAlreadyExistsException)
  - [ ] Create new User entity from request
  - [ ] Hash password using passwordEncoder.encode()
  - [ ] Set enabled to true by default
  - [ ] Save user using repository
  - [ ] Generate JWT token for new user (authenticate first)
  - [ ] Return AuthResponse with token and user info

- [ ] 3. Implement login method
  - [ ] Method signature: `AuthResponse login(LoginRequest request)`
  - [ ] Create Authentication object with email and password
  - [ ] Authenticate using authenticationManager.authenticate()
  - [ ] This will throw BadCredentialsException if invalid
  - [ ] If successful, generate JWT token
  - [ ] Fetch user details from database
  - [ ] Return AuthResponse with token and user info

- [ ] 4. Implement getCurrentUser method
  - [ ] Method signature: `UserDTO getCurrentUser(String email)`
  - [ ] Fetch user by email
  - [ ] Throw exception if not found
  - [ ] Convert User entity to UserDTO
  - [ ] Return UserDTO

- [ ] 5. Create custom exceptions
  - [ ] Create `exception/EmailAlreadyExistsException.java`
  - [ ] Create `exception/AuthenticationFailedException.java` (optional, can use Spring's)

**Acceptance Criteria**:
- AuthService created
- Register method works and hashes password
- Login method authenticates and generates token
- getCurrentUser method returns user info
- Exceptions thrown for error cases
- Passwords never stored in plain text

---

## PR #39: Auth Controller

**PRD Reference**: Phase IV, Deliverable 1 - Auth Endpoints (Controller)

### Tasks

- [ ] 1. Create AuthController class
  - [ ] Create `controller/AuthController.java`
  - [ ] Add `@RestController` annotation
  - [ ] Add `@RequestMapping("/api/auth")` annotation
  - [ ] Inject AuthService

- [ ] 2. Implement POST /api/auth/register endpoint
  - [ ] Add method with `@PostMapping("/register")` annotation
  - [ ] Method signature: `ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request)`
  - [ ] Call authService.register(request)
  - [ ] Return ResponseEntity.status(HttpStatus.CREATED).body(response)

- [ ] 3. Implement POST /api/auth/login endpoint
  - [ ] Add method with `@PostMapping("/login")` annotation
  - [ ] Method signature: `ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request)`
  - [ ] Call authService.login(request)
  - [ ] Return ResponseEntity.ok(response)

- [ ] 4. Implement GET /api/auth/me endpoint
  - [ ] Add method with `@GetMapping("/me")` annotation
  - [ ] Method signature: `ResponseEntity<UserDTO> getCurrentUser(Authentication authentication)`
  - [ ] Get email from authentication.getName()
  - [ ] Call authService.getCurrentUser(email)
  - [ ] Return ResponseEntity.ok(userDTO)

**Acceptance Criteria**:
- AuthController created
- Register endpoint implemented
- Login endpoint implemented
- Get current user endpoint implemented
- All endpoints use proper HTTP methods and status codes
- Validation works on request bodies

---

## PR #40: Auth Exception Handling

**PRD Reference**: Phase IV, Deliverable 1 - Exception Handling

### Tasks

- [ ] 1. Update GlobalExceptionHandler for auth exceptions
  - [ ] Open `exception/GlobalExceptionHandler.java`

- [ ] 2. Handle EmailAlreadyExistsException
  - [ ] Add method with `@ExceptionHandler(EmailAlreadyExistsException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.CONFLICT)` (409)
  - [ ] Return ErrorResponse with message

- [ ] 3. Handle BadCredentialsException (login failures)
  - [ ] Add method with `@ExceptionHandler(BadCredentialsException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.UNAUTHORIZED)` (401)
  - [ ] Return ErrorResponse with message: "Invalid email or password"

- [ ] 4. Handle AuthenticationException (general auth failures)
  - [ ] Add method with `@ExceptionHandler(AuthenticationException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.UNAUTHORIZED)` (401)
  - [ ] Return ErrorResponse

- [ ] 5. Handle AccessDeniedException (insufficient permissions)
  - [ ] Add method with `@ExceptionHandler(AccessDeniedException.class)`
  - [ ] Add `@ResponseStatus(HttpStatus.FORBIDDEN)` (403)
  - [ ] Return ErrorResponse with message: "You don't have permission to access this resource"

**Acceptance Criteria**:
- Auth exceptions handled globally
- Proper HTTP status codes returned
- Clear error messages
- Password errors don't expose system details

---

## Phase IV Part 1 Manual Testing Checklist

### User Registration
- [ ] Test: POST /api/auth/register with valid data returns 201 and JWT token
- [ ] Test: Verify password is hashed in database (not plain text)
- [ ] Test: Register with existing email returns 409 Conflict
- [ ] Test: Register with invalid email format returns 400
- [ ] Test: Register with short password returns 400
- [ ] Test: Register without required fields returns 400

### User Login
- [ ] Test: POST /api/auth/login with correct credentials returns 200 and JWT token
- [ ] Test: Login with incorrect password returns 401
- [ ] Test: Login with non-existent email returns 401
- [ ] Test: Login with invalid email format returns 400

### JWT Token
- [ ] Test: Use generated token in Authorization header (Bearer {token})
- [ ] Test: Access protected endpoint with valid token succeeds
- [ ] Test: Access protected endpoint without token returns 401
- [ ] Test: Access protected endpoint with expired token returns 401
- [ ] Test: Access protected endpoint with invalid token returns 401

### Current User Endpoint
- [ ] Test: GET /api/auth/me with valid token returns user info
- [ ] Test: GET /api/auth/me without token returns 401
- [ ] Verify response contains: email, fullName, role, etc.

### Access Control
- [ ] Create users with different roles (ADMIN, RECRUITER, JOB_SEEKER)
- [ ] Test: Anonymous can GET /api/jobs (public)
- [ ] Test: Anonymous can GET /api/companies (public)
- [ ] Test: Anonymous cannot POST /api/jobs (returns 401)
- [ ] Test: JOB_SEEKER cannot POST /api/jobs (returns 403)
- [ ] Test: RECRUITER can POST /api/jobs (returns 201)
- [ ] Test: ADMIN can POST /api/jobs (returns 201)
- [ ] Test: RECRUITER can DELETE their own jobs
- [ ] Test: JOB_SEEKER can access /api/applications (when implemented)

### Edge Cases
- [ ] Test: Token in header without "Bearer " prefix rejected
- [ ] Test: Malformed token rejected
- [ ] Test: Token with modified signature rejected
- [ ] Test: Disabled user cannot login

---

## Phase IV Part 1 Completion Checklist

- [ ] All PRs merged
- [ ] JWT dependencies added
- [ ] User entity and repository created
- [ ] Password hashing with BCrypt working
- [ ] JWT token generation working
- [ ] JWT token validation working
- [ ] Spring Security configured correctly
- [ ] Public endpoints accessible without auth
- [ ] Protected endpoints require auth
- [ ] Role-based access control working
- [ ] Register endpoint working
- [ ] Login endpoint working
- [ ] Get current user endpoint working
- [ ] All auth exception scenarios handled
- [ ] Passwords never logged or exposed
- [ ] All manual tests passing

**When all items checked**: Phase IV Part 1 is complete! Proceed to Phase IV Part 2 (File Upload & Applications).
