package com.jobboard.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.lang.reflect.Method;

public class SalaryRangeValidator implements ConstraintValidator<ValidSalaryRange, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            Method getMin = value.getClass().getMethod("getSalaryMin");
            Method getMax = value.getClass().getMethod("getSalaryMax");
            BigDecimal min = (BigDecimal) getMin.invoke(value);
            BigDecimal max = (BigDecimal) getMax.invoke(value);
            if (min == null || max == null) {
                return true;
            }
            return max.compareTo(min) >= 0;
        } catch (Exception e) {
            return true;
        }
    }
}
