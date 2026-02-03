# Phase I Manual Testing

Base URL: `http://localhost:8080`

**Prereqs:** `docker-compose up -d` then `./mvnw spring-boot:run`

---

## 1. Companies

- [x] **POST** – create company

```bash
curl -s -X POST http://localhost:8080/api/companies \
  -H "Content-Type: application/json" \
  -d '{"name":"Tech Corp","description":"We build stuff","website":"https://techcorp.com","location":"New York, NY"}'
```

Expect: **201** + JSON body with `id`, `name`, `createdAt`, `updatedAt`, etc.

- [x] **GET all** companies

```bash
curl -s http://localhost:8080/api/companies
```

Expect: **200** + JSON array.

- [x] **GET** company by id (replace `1` with real id)

```bash
curl -s http://localhost:8080/api/companies/1
```

Expect: **200** + single company JSON.

- [x] **PATCH** company (replace `1` with real id)

```bash
curl -s -X PATCH http://localhost:8080/api/companies/3 \
  -H "Content-Type: application/json" \
  -d '{"name":"Tech Corp Updated!!!"}'
```

Expect: **200** + updated company JSON. Then GET by id again to confirm.

- [x] **DELETE** company (replace `1` with real id)

```bash
curl -s -X DELETE http://localhost:8080/api/companies/1
```

Expect: **204** no content.

---

## 2. Jobs (use a company id from Companies section)

### POST – create job (replace `1` with company id)

```bash
curl -s -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"title":"Senior Java Dev","description":"Build APIs","companyId":1,"location":"New York, NY","jobType":"FULL_TIME","experienceLevel":"SENIOR","remoteOption":"HYBRID"}'
```

Expect: **201** + JSON body with job + embedded `company` (id, name, location).

### GET all jobs

```bash
curl -s http://localhost:8080/api/jobs
```

Expect: **200** + array of jobs, each with embedded `company`.

### GET job by id – full details (replace `1` with job id)

```bash
curl -s http://localhost:8080/api/jobs/1
```

Expect: **200** + JobDetailDTO (includes description, expiryDate, applicationUrl, createdAt, updatedAt).

### PATCH job (replace `1` with job id)

```bash
curl -s -X PATCH http://localhost:8080/api/jobs/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Lead Java Developer"}'
```

Expect: **200** + updated job JSON.

### DELETE job (replace `1` with job id)

```bash
curl -s -X DELETE http://localhost:8080/api/jobs/1
```

Expect: **204** no content.

---

## 3. Error cases

### 404 – company not found

```bash
curl -s http://localhost:8080/api/companies/99999
```

Expect: **404** + body with `message` like "Company not found with id: 99999".

### 404 – job not found

```bash
curl -s http://localhost:8080/api/jobs/99999
```

Expect: **404** + body with `message` like "Job not found with id: 99999".

### 400 – validation (e.g. blank name)

```bash
curl -s -X POST http://localhost:8080/api/companies \
  -H "Content-Type: application/json" \
  -d '{"name":"","location":"NYC"}'
```

Expect: **400** + body with `errors` map (field → message).

### DELETE then GET – 404

After deleting a company or job, GET same id:

```bash
curl -s -X DELETE http://localhost:8080/api/companies/1
curl -s http://localhost:8080/api/companies/1
```

Second call: **404**.

```bash
curl -s -X DELETE http://localhost:8080/api/jobs/1
curl -s http://localhost:8080/api/jobs/1
```

Second call: **404**.

---

## 4. Quick checklist

| Check              | Endpoint / action                                                  |
| ------------------ | ------------------------------------------------------------------ |
| 201 + body         | POST company, POST job                                             |
| 200 + list         | GET /api/companies, GET /api/jobs                                  |
| 200 + single       | GET /api/companies/{id}, GET /api/jobs/{id} (JobDetailDTO for job) |
| 200 + updated      | PATCH company, PATCH job                                           |
| 204                | DELETE company, DELETE job                                         |
| 404 + message      | GET non-existent id                                                |
| 400 + field errors | POST/PATCH with invalid or missing required fields                 |
