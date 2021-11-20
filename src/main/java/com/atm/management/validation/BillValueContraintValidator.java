package com.atm.management.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class BillValueContraintValidator implements ConstraintValidator<BillValue, Integer> {

    @Override
    public void initialize(BillValue constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        List<Integer> billValues = Arrays.asList(1, 5, 10, 50, 100, 200, 500);
        return billValues.contains(value);
    }
}
