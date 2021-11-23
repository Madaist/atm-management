package com.atm.management.dto;

import java.util.HashMap;

public class AtmCashWithdrawalResponseDTO {

    HashMap<Integer, Integer> bills;

    public AtmCashWithdrawalResponseDTO(HashMap<Integer, Integer> bills) {
        this.bills = bills;
    }

    public HashMap<Integer, Integer> getBills() {
        return bills;
    }

    public void setBills(HashMap<Integer, Integer> bills) {
        this.bills = bills;
    }
}
