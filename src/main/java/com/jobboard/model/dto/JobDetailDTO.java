package com.jobboard.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class JobDetailDTO extends JobDTO {

    private String description;
    private LocalDateTime expiryDate;
    private String applicationUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
