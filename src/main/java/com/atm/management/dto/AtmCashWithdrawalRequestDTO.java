package com.atm.management.dto;

public class AtmCashWithdrawalRequestDTO {

    private int amount;

    public AtmCashWithdrawalRequestDTO() {
    }

    public AtmCashWithdrawalRequestDTO(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
