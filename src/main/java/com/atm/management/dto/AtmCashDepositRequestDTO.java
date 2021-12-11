package com.atm.management.dto;

import java.util.Objects;

public class AtmCashDepositRequestDTO {

    private int atmId;
    private int billValue;
    private int billCount;

    public AtmCashDepositRequestDTO(int billValue, int billCount) {
        this.atmId = atmId;
        this.billValue = billValue;
        this.billCount = billCount;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtmCashDepositRequestDTO that = (AtmCashDepositRequestDTO) o;
        return atmId == that.atmId && billValue == that.billValue && billCount == that.billCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(atmId, billValue, billCount);
    }
}
