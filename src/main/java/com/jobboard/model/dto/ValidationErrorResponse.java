package com.jobboard.model.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {

    private Map<String, String> errors;

    public ValidationErrorResponse(String message, int status, Map<String, String> errors, LocalDateTime timestamp) {
        super(message, status, timestamp);
        this.errors = errors;
    }
}
