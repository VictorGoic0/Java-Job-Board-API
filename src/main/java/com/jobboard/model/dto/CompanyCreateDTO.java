package com.jobboard.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCreateDTO {

    @NotBlank(message = "Company name is required")
    private String name;

    private String description;

    @Pattern(regexp = "^(https?://).*", message = "Website must be a valid URL")
    private String website;

    @NotBlank(message = "Location is required")
    private String location;
}
