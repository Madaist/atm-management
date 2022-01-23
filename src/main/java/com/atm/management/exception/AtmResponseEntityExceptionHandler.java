package com.atm.management.exception;

import com.atm.management.constants.MessageConstants;
import com.atm.management.constants.ResponseConstants;
import com.atm.management.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@RestControllerAdvice
public class AtmResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ApiResponse> handleConstraintViolation(ConstraintViolationException ex) {

        ApiResponse exception = null;
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            String message = constraintViolation.getMessage();

            switch (message) {
                case MessageConstants.BILL_VALUE_ERROR_MESSAGE: {
                    exception = new ApiResponse(
                            ResponseConstants.INVALID_BILL_VALUE.getStatus(),
                            ResponseConstants.INVALID_BILL_VALUE.getCode(),
                            ResponseConstants.INVALID_BILL_VALUE.getMessage());
                    break;
                }
                case MessageConstants.MUST_BE_LESS_THAN_100000:
                case MessageConstants.MUST_BE_GREATER_THAN_0: {
                    exception = new ApiResponse(
                            ResponseConstants.INVALID_BILL_COUNT.getStatus(),
                            ResponseConstants.INVALID_BILL_COUNT.getCode(),
                            ResponseConstants.INVALID_BILL_COUNT.getMessage());
                    break;
                }
                default: {
                    exception = new ApiResponse(
                            ResponseConstants.BAD_REQUEST.getStatus(),
                            ResponseConstants.BAD_REQUEST.getCode(),
                            ResponseConstants.BAD_REQUEST.getMessage());
                }
            }
        }
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {DuplicateBillValuesException.class})
    public ResponseEntity<ApiResponse> handleDuplicateBillValuesException() {

        ApiResponse exception = new ApiResponse(
                ResponseConstants.DUPLICATE_BILL_VALUES.getStatus(),
                ResponseConstants.DUPLICATE_BILL_VALUES.getCode(),
                ResponseConstants.DUPLICATE_BILL_VALUES.getMessage());

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RequestSizeExceededException.class})
    public ResponseEntity<ApiResponse> handleRequestSizeExceededException() {

        ApiResponse exception = new ApiResponse(
                ResponseConstants.REQUEST_SIZE_EXCEEDED.getStatus(),
                ResponseConstants.REQUEST_SIZE_EXCEEDED.getCode(),
                ResponseConstants.REQUEST_SIZE_EXCEEDED.getMessage());

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AtmCapacityExceededException.class})
    public ResponseEntity<ApiResponse> handleAtmCapacityExceededException() {

        ApiResponse exception = new ApiResponse(
                ResponseConstants.ATM_CAPACITY_EXCEEDED.getStatus(),
                ResponseConstants.ATM_CAPACITY_EXCEEDED.getCode(),
                ResponseConstants.ATM_CAPACITY_EXCEEDED.getMessage());

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AtmNotFoundException.class})
    public ResponseEntity<ApiResponse> handleAtmNotFoundException() {

        ApiResponse exception = new ApiResponse(
                ResponseConstants.ATM_NOT_FOUND.getStatus(),
                ResponseConstants.ATM_NOT_FOUND.getCode(),
                ResponseConstants.ATM_NOT_FOUND.getMessage());

        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(value = {ImpossibleBillCombinationException.class})
    public ResponseEntity<ApiResponse> handleImpossibleBillCombinationException() {

        ApiResponse exception = new ApiResponse(
                ResponseConstants.IMPOSSIBLE_BILLS_COMBINATION.getStatus(),
                ResponseConstants.IMPOSSIBLE_BILLS_COMBINATION.getCode(),
                ResponseConstants.IMPOSSIBLE_BILLS_COMBINATION.getMessage());

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AmountExceedsAtmCashException.class})
    public ResponseEntity<ApiResponse> handleAmountExceedsAtmCashException() {

        ApiResponse exception = new ApiResponse(
                ResponseConstants.REQUESTED_AMOUNT_TOO_LARGE.getStatus(),
                ResponseConstants.REQUESTED_AMOUNT_TOO_LARGE.getCode(),
                ResponseConstants.REQUESTED_AMOUNT_TOO_LARGE.getMessage());

        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {NegativeAmountException.class})
    public ResponseEntity<ApiResponse> handleNegativeAmountException() {

        ApiResponse exception = new ApiResponse(
                ResponseConstants.REQUESTED_NEGATIVE_AMOUNT.getStatus(),
                ResponseConstants.REQUESTED_NEGATIVE_AMOUNT.getCode(),
                ResponseConstants.REQUESTED_NEGATIVE_AMOUNT.getMessage());

        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }
}
