package com.atm.management.constants;

public enum ResponseConstants {

    INVALID_BILL_VALUE("failed", "INVALID_BILL_VALUE", MessageConstants.BILL_VALUE_ERROR_MESSAGE),
    DEPOSIT_SUCCEEDED("success", "DEPOSIT_SUCCESSFUL", MessageConstants.DEPOSIT_SUCCEEDED),
    DUPLICATE_BILL_VALUES("failed", "DUPLICATE_BILL_VALUES", MessageConstants.DUPLICATE_BILL_VALUES),
    BAD_REQUEST("failed", "BAD_REQUEST", MessageConstants.BAD_REQUEST),
    INVALID_BILL_COUNT("failed", "INVALID_BILL_COUNT", MessageConstants.INVALID_BILL_COUNT),
    REQUEST_SIZE_EXCEEDED("failed", "REQUEST_SIZE_EXCEEDED", MessageConstants.REQUEST_SIZE_EXCEEDED),
    ATM_CAPACITY_EXCEEDED("failed", "ATM_CAPACITY_EXCEEDED", MessageConstants.ATM_CAPACITY_EXCEEDED);

    private final String status;
    private final String code;
    private final String message;

    ResponseConstants(String status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}

