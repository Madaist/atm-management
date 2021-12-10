package com.atm.management.validation;

import com.atm.management.constants.MessageConstants;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BillValueContraintValidator.class)
public @interface BillValue {

    String message() default MessageConstants.BILL_VALUE_ERROR_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] value() default {};
}
