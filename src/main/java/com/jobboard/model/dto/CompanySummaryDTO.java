package com.jobboard.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanySummaryDTO {

    private Long id;
    private String name;
    private String location;
}
