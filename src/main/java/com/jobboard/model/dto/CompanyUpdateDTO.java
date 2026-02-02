package com.jobboard.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateDTO {

    @NotBlank(message = "Company name must not be blank when provided")
    private String name;

    private String description;

    @Pattern(regexp = "^(https?://).*", message = "Website must be a valid URL when provided")
    private String website;

    @NotBlank(message = "Location must not be blank when provided")
    private String location;
}
