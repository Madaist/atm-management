package com.atm.management.constants;

public class MessageConstants {

    public static final String BILL_VALUE_ERROR_MESSAGE = "Bill value should only be 1, 5, 10, 50, 100, 200 or 500";
    public static final String MUST_BE_LESS_THAN_100000 = "must be less than or equal to 100000";
    public static final String MUST_BE_GREATER_THAN_0 = "must be greater than or equal to 1";

    public static final String BAD_REQUEST = "Invalid request. PLease, validate your request and try again.";
    public static final String DEPOSIT_SUCCEEDED = "Cash was successfully added";
    public static final String INVALID_BILL_COUNT = "Number of bills should be between 1 and 1000000";

    public static final String REQUEST_SIZE_EXCEEDED = "The size of the request is too large. Please, introduce a number of pairs bill value : bill count smaller than 100";
    public static final String DUPLICATE_BILL_VALUES = "The request contains duplicate bill values.";
    public static final String ATM_CAPACITY_EXCEEDED = "The atm has a capacity of 100.000 bills";
}
