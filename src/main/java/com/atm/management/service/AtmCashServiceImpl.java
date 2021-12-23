package com.atm.management.service;

import com.atm.management.constants.ResponseConstants;
import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.exception.*;
import com.atm.management.model.Atm;
import com.atm.management.model.AtmCash;
import com.atm.management.dto.AtmCashDepositResponseDTO;
import com.atm.management.repository.AtmCashDAO;
import com.atm.management.repository.AtmDAO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.SerializationUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AtmCashServiceImpl implements AtmCashService {

    private final AtmCashDAO atmCashDAO;
    private final AtmDAO atmDAO;
    private final ModelMapper modelMapper;

    public AtmCashServiceImpl(AtmCashDAO atmCashDAO, AtmDAO atmDAO, ModelMapper modelMapper) {
        this.atmCashDAO = atmCashDAO;
        this.atmDAO = atmDAO;
        this.modelMapper = modelMapper;
    }

    public AtmCashDepositResponseDTO addCash(List<AtmCashDepositRequestDTO> request, int atmId) {

        Optional<Atm> atm = atmDAO.findById(atmId);
        if (!atm.isPresent()) {
            throw new AtmNotFoundException();
        }

        validateDepositRequest(request, atmId);

        for (AtmCashDepositRequestDTO cash : request) {
            AtmCash atmCash = modelMapper.map(cash, AtmCash.class);
            atmCash.setAtm(atm.get());

            AtmCash existingBill = atmCashDAO.findByBillValueAndAtmId(
                    atmCash.getBillValue(),
                    atm.get().getId());

            if (existingBill != null) {
                int newBillCount = existingBill.getBillCount() + cash.getBillCount();
                existingBill.setBillCount(newBillCount);
            } else {
                existingBill = atmCash;
            }
            atmCashDAO.save(existingBill);
        }

        return new AtmCashDepositResponseDTO(
                ResponseConstants.DEPOSIT_SUCCEEDED.getStatus(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getCode(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getMessage());
    }

    @Override
    public Map<Integer, Integer> withdrawCash(AtmCashWithdrawalRequestDTO request, int atmId) {

        validateWithdrawalRequest(request, atmId);

        int[] bills = computeNeededBills(request.getAmount(), atmId);

        if (bills != null) {

            Map<Integer, Integer> mappedBills = computePairResponse(bills);
            decreaseAtmAvailableCash(mappedBills, atmId);
            return mappedBills;

        } else
            throw new ImpossibleBillCombinationException();
    }

    private void validateWithdrawalRequest(AtmCashWithdrawalRequestDTO request, int atmId) {

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

    private int[] computeNeededBills(int amount, int atmId) {

        List<AtmCash> initialAtmCash = atmCashDAO.findByAtmId(atmId);
        List<AtmCash> processedAtmCash = new ArrayList<>();
        for (AtmCash atmCash : initialAtmCash) {
            processedAtmCash.add((AtmCash) SerializationUtils.clone(atmCash));
        }

        List<Integer> denominations = initialAtmCash.stream()
                .map(AtmCash::getBillValue)
                .collect(Collectors.toList());

        int availableBillsCount = denominations.size();
        int[] count = new int[amount + 1];
        int[] from = new int[amount + 1];

        count[0] = 1;
        for (int i = 0; i < amount; i++)
            if (count[i] > 0)
                for (int j = 0; j < availableBillsCount; j++) {

                    AtmCash currentBill = processedAtmCash.get(j);
                    if (currentBill.getBillCount() > 0) {

                        int p = i + denominations.get(j);
                        if (p <= amount) {
                            if (count[p] == 0 || count[p] > count[i] + 1) {
                                count[p] = count[i] + 1;
                                from[p] = j;
                                currentBill.setBillCount(currentBill.getBillCount() - 1);
                            }
                        }
                    }
                }

        // No solutions:
        if (count[amount] == 0)
            return null;

        // Build answer.
        int[] result = new int[count[amount] - 1];
        int k = amount;
        while (k > 0) {
            result[count[k] - 2] = denominations.get(from[k]);
            k = k - denominations.get(from[k]);
        }

        return result;
    }

    private Map<Integer, Integer> computePairResponse(int[] coins) {
        TreeMap<Integer, Integer> mappedBills = new TreeMap<>(Collections.reverseOrder());

        for (int coin : coins) {
            if (mappedBills.containsKey(coin)) {
                int frequency = mappedBills.get(coin);
                mappedBills.put(coin, ++frequency);
            } else {
                mappedBills.put(coin, 1);
            }
        }

        return mappedBills;
    }

    public void decreaseAtmAvailableCash(Map<Integer, Integer> withdrawnCash, int atmId) {

        for (Map.Entry<Integer, Integer> entry : withdrawnCash.entrySet()) {

            Integer billValue = entry.getKey();
            Integer billCount = entry.getValue();

            AtmCash bill = atmCashDAO.findByBillValueAndAtmId(billValue, atmId);
            bill.setBillCount(bill.getBillCount() - billCount);

            atmCashDAO.save(bill);
        }
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

        int ATM_CAPACITY = 100000;
        if (totalBillsCountFromRequest + totalBillsCountFromAtm > ATM_CAPACITY) {
            throw new AtmCapacityExceededException();
        }
    }
}
