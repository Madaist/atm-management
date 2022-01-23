package com.atm.management.service;

import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.dto.ApiResponse;

import java.util.List;
import java.util.Map;

public interface AtmCashService {

    ApiResponse addCash(List<AtmCashDepositRequestDTO> request, int atmId);

    Map<Integer, Integer> withdrawCash(AtmCashWithdrawalRequestDTO request, int id);
}
