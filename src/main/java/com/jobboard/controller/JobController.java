package com.jobboard.controller;

import com.jobboard.model.dto.JobCreateDTO;
import com.jobboard.model.dto.JobDetailDTO;
import com.jobboard.model.dto.JobDTO;
import com.jobboard.model.dto.JobUpdateDTO;
import com.jobboard.service.JobService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
@Validated
public class JobController {

    private static final Set<String> ALLOWED_JOB_SORT_FIELDS = Set.of(
            "id", "title", "location", "salaryMin", "salaryMax", "jobType",
            "experienceLevel", "remoteOption", "postedDate", "isActive", "createdAt", "updatedAt");

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<Page<JobDTO>> getAllJobs(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "postedDate,desc") String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return ResponseEntity.ok(jobService.getAllJobs(pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<JobDTO>> getActiveJobs(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "postedDate,desc") String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return ResponseEntity.ok(jobService.getActiveJobs(pageable));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "postedDate");
        }
        String[] parts = sort.split(",");
        if (parts.length < 2) {
            return Sort.by(Sort.Direction.DESC, "postedDate");
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (int i = 0; i + 1 < parts.length; i += 2) {
            String property = parts[i].trim();
            if (!ALLOWED_JOB_SORT_FIELDS.contains(property)) {
                continue;
            }
            Sort.Direction direction = "asc".equalsIgnoreCase(parts[i + 1].trim())
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            orders.add(new Sort.Order(direction, property));
        }
        return orders.isEmpty() ? Sort.by(Sort.Direction.DESC, "postedDate") : Sort.by(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDetailDTO> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PostMapping
    public ResponseEntity<JobDTO> createJob(@Valid @RequestBody JobCreateDTO dto) {
        JobDTO result = jobService.createJob(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobUpdateDTO dto) {
        return ResponseEntity.ok(jobService.updateJob(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}
