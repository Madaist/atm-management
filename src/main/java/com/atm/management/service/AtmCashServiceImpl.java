package com.atm.management.service;

import com.atm.management.constants.ResponseConstants;
import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalResponseDTO;
import com.atm.management.exception.AtmCapacityExceededException;
import com.atm.management.exception.DuplicateBillValuesException;
import com.atm.management.exception.RequestSizeExceededException;
import com.atm.management.model.Atm;
import com.atm.management.model.AtmCash;
import com.atm.management.dto.AtmCashDepositResponseDTO;
import com.atm.management.repository.AtmCashDAO;
import com.atm.management.repository.AtmDAO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AtmCashServiceImpl implements AtmCashService {

    private final AtmCashDAO atmCashDAO;
    private final AtmDAO atmDAO;
    private final ModelMapper modelMapper;
    private final int ATM_CAPACITY = 100000;

    public AtmCashServiceImpl(AtmCashDAO atmCashDAO, AtmDAO atmDAO, ModelMapper modelMapper) {
        this.atmCashDAO = atmCashDAO;
        this.atmDAO = atmDAO;
        this.modelMapper = modelMapper;
    }

    public AtmCashDepositResponseDTO addCash(List<AtmCashDepositRequestDTO> request, int atmId) {

        validateDepositRequest(request, atmId);
        Atm atm = atmDAO.getById(atmId);

        for (AtmCashDepositRequestDTO cash : request) {
            AtmCash atmCash = modelMapper.map(cash, AtmCash.class);
            atmCash.setAtm(atm);

            AtmCash existingBill = atmCashDAO.findByBillValueAndAtmId(atmCash.getBillValue(), atm.getId());

            if (existingBill != null) {
                int newBillCount = existingBill.getBillCount() + cash.getBillCount();
                existingBill.setBillCount(newBillCount);
            } else {
                existingBill = atmCash;
            }
            atmCashDAO.save(existingBill);
        }

        return new AtmCashDepositResponseDTO(ResponseConstants.DEPOSIT_SUCCEEDED.getStatus(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getCode(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getMessage());
    }

    @Override
    public AtmCashWithdrawalResponseDTO withdrawCash(List<AtmCashWithdrawalRequestDTO> request, int id) {

        return null;
    }

    private void validateDepositRequest(List<AtmCashDepositRequestDTO> request, int atmId) {

        // Check request size
        if (request.size() > 100) {
            throw new RequestSizeExceededException();
        }

        // Check if there are duplicates in the request
        Set<Integer> billValuesSet = request.stream()
                .map(AtmCashDepositRequestDTO::getBillValue)
                .collect(Collectors.toSet());
        if (billValuesSet.size() != request.size()) {
            throw new DuplicateBillValuesException();
        }

        // Check if the request will exceed atm's capacity
        List<AtmCash> atmCash = atmCashDAO.findByAtmId(atmId);
        Integer totalBillsCountFromAtm = atmCash.stream()
                .map(AtmCash::getBillCount)
                .mapToInt(Integer::intValue)
                .sum();
        Integer totalBillsCountFromRequest = request.stream()
                .map(AtmCashDepositRequestDTO::getBillCount)
                .mapToInt(Integer::intValue)
                .sum();

        if (totalBillsCountFromRequest + totalBillsCountFromAtm > ATM_CAPACITY) {
            throw new AtmCapacityExceededException();
        }
    }
}
