package com.jobboard.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {

    private Long id;
    private String name;
    private String description;
    private String website;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
