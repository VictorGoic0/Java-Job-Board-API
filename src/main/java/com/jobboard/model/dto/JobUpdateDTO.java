package com.jobboard.model.dto;

import com.jobboard.model.entity.ExperienceLevel;
import com.jobboard.model.entity.JobType;
import com.jobboard.model.entity.RemoteOption;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
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
public class JobUpdateDTO {

    private String title;

    private String description;

    private Long companyId;

    private String location;

    @DecimalMin(value = "0.0", message = "Minimum salary must be positive when provided")
    private BigDecimal salaryMin;

    @DecimalMin(value = "0.0", message = "Maximum salary must be positive when provided")
    private BigDecimal salaryMax;

    private JobType jobType;

    private ExperienceLevel experienceLevel;

    private RemoteOption remoteOption;

    @Future(message = "Expiry date must be in the future when provided")
    private LocalDateTime expiryDate;

    @Pattern(regexp = "^(https?://).*", message = "Application URL must be valid when provided")
    private String applicationUrl;
}
