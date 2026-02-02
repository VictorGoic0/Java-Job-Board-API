package com.jobboard.model.dto;

import com.jobboard.model.entity.ExperienceLevel;
import com.jobboard.model.entity.JobType;
import com.jobboard.model.entity.RemoteOption;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {

    private Long id;
    private String title;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private RemoteOption remoteOption;
    private LocalDateTime postedDate;
    private Boolean isActive;
    private CompanySummaryDTO company;
}
