package com.jobboard.model.dto;

import com.jobboard.model.entity.ExperienceLevel;
import com.jobboard.model.entity.JobType;
import com.jobboard.model.entity.RemoteOption;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import com.jobboard.validation.ValidSalaryRange;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidSalaryRange
public class JobCreateDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Company is required")
    private Long companyId;

    @NotBlank(message = "Location is required")
    private String location;

    @DecimalMin(value = "0.0", message = "Minimum salary must be positive")
    private BigDecimal salaryMin;

    @DecimalMin(value = "0.0", message = "Maximum salary must be positive")
    private BigDecimal salaryMax;

    @NotNull(message = "Job type is required")
    private JobType jobType;

    @NotNull(message = "Experience level is required")
    private ExperienceLevel experienceLevel;

    @NotNull(message = "Remote option is required")
    private RemoteOption remoteOption;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;

    @Pattern(regexp = "^(https?://).*", message = "Application URL must be valid")
    private String applicationUrl;
}
