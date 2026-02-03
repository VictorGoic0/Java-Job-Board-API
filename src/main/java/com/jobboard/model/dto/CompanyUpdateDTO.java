package com.jobboard.model.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUpdateDTO {

    /** When provided, must not be blank (mapper skips blank). */
    private String name;

    private String description;

    @Pattern(regexp = "^(https?://).*", message = "Website must be a valid URL when provided")
    private String website;

    /** When provided, must not be blank (mapper skips blank). */
    private String location;
}
