package com.atm.management.dto;

public class AtmCashDTO {

    private int atmId;
    private int billValue;
    private int billCount;

    public int getBillValue() {
        return billValue;
    }

    public void setBillValue(int billValue) {
        this.billValue = billValue;
    }

    public int getBillCount() {
        return billCount;
    }

    public void setBillCount(int billCount) {
        this.billCount = billCount;
    }

    public int getAtmId() {
        return atmId;
    }

    public void setAtmId(int atmId) {
        this.atmId = atmId;
    }
}
