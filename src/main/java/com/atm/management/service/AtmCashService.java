package com.atm.management.service;

import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalResponseDTO;
import com.atm.management.exception.AtmCapacityExceededException;
import com.atm.management.exception.DuplicateBillValuesException;
import com.atm.management.exception.RequestSizeExceededException;
import com.atm.management.dto.AtmCashDepositResponseDTO;

import java.util.List;
import java.util.Map;

public interface AtmCashService {

    AtmCashDepositResponseDTO addCash(List<AtmCashDepositRequestDTO> request, int atmId);

    Map<Integer, Integer> withdrawCash(AtmCashWithdrawalRequestDTO request, int id);
}
