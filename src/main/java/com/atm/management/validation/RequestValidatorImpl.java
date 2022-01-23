package com.atm.management.validation;

import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.exception.*;
import com.atm.management.model.Atm;
import com.atm.management.model.AtmCash;
import com.atm.management.repository.AtmCashDAO;
import com.atm.management.repository.AtmDAO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RequestValidatorImpl implements RequestValidator {

    private final AtmCashDAO atmCashDAO;
    private final AtmDAO atmDAO;

    public RequestValidatorImpl(AtmCashDAO atmCashDAO, AtmDAO atmDAO) {
        this.atmCashDAO = atmCashDAO;
        this.atmDAO = atmDAO;
    }


    public void validateDepositRequest(List<AtmCashDepositRequestDTO> request, int atmId) {

        Optional<Atm> atm = atmDAO.findById(atmId);
        if (!atm.isPresent()) {
            throw new AtmNotFoundException();
        }

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

        int ATM_CAPACITY = 100000;
        if (totalBillsCountFromRequest + totalBillsCountFromAtm > ATM_CAPACITY) {
            throw new AtmCapacityExceededException();
        }
    }

    public void validateWithdrawalRequest(AtmCashWithdrawalRequestDTO request, int atmId) {

        Optional<Atm> atm = atmDAO.findById(atmId);
        if (!atm.isPresent()) {
            throw new AtmNotFoundException();
        }

        if (request.getAmount() <= 0) {
            throw new NegativeAmountException();
        }

        List<AtmCash> atmCash = atmCashDAO.findByAtmId(atmId);
        int totalAtmCash = atmCash.stream()
                .map(a -> a.getBillValue() * a.getBillCount())
                .mapToInt(Integer::intValue)
                .sum();

        if (request.getAmount() > totalAtmCash) {
            throw new AmountExceedsAtmCashException();
        }
    }
}
