package com.jobboard.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(
    name = "job",
    indexes = {
        @jakarta.persistence.Index(name = "idx_job_company_id", columnList = "company_id"),
        @jakarta.persistence.Index(name = "idx_job_is_active", columnList = "is_active"),
        @jakarta.persistence.Index(name = "idx_job_posted_date", columnList = "posted_date")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @NotNull(message = "Company is required")
    private Company company;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    @DecimalMin(value = "0.0", message = "Minimum salary must be positive")
    @Column(name = "salary_min", precision = 10, scale = 2)
    private BigDecimal salaryMin;

    @DecimalMin(value = "0.0", message = "Maximum salary must be positive")
    @Column(name = "salary_max", precision = 10, scale = 2)
    private BigDecimal salaryMax;

    @NotNull(message = "Job type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    @NotNull(message = "Experience level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false)
    private ExperienceLevel experienceLevel;

    @NotNull(message = "Remote option is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "remote_option", nullable = false)
    private RemoteOption remoteOption;

    @Column(name = "posted_date", nullable = false)
    private LocalDateTime postedDate;

    @Future(message = "Expiry date must be in the future")
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Pattern(regexp = "^(https?://).*", message = "Application URL must be valid")
    @Column(name = "application_url", length = 500)
    private String applicationUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Integer version;

    @PrePersist
    protected void onCreate() {
        if (postedDate == null) {
            postedDate = LocalDateTime.now();
        }
    }
}
