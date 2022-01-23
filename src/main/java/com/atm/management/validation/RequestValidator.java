package com.atm.management.validation;

import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import org.springframework.stereotype.Component;

import java.util.List;

public interface RequestValidator {

    void validateDepositRequest(List<AtmCashDepositRequestDTO> request, int atmId);
    void validateWithdrawalRequest(AtmCashWithdrawalRequestDTO request, int atmId);
}
