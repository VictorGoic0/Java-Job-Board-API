package com.jobboard.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = SalaryRangeValidator.class)
public @interface ValidSalaryRange {

    String message() default "Maximum salary must be greater than or equal to minimum salary";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
