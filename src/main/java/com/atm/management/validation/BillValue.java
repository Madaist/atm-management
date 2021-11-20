package com.atm.management.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = BillValueContraintValidator.class)
public @interface BillValue {

    String message() default "Bill value should only be 1, 5, 10, 50, 100, 200 or 500";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] value() default {};
}
