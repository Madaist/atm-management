package com.atm.management.dto;

public class AtmCashWithdrawalRequestDTO {

    public int amount;

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
