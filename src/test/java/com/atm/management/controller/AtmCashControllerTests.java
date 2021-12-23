package com.atm.management.controller;

import com.atm.management.constants.ResponseConstants;
import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashDepositResponseDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.exception.*;
import com.atm.management.service.AtmCashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AtmCashControllerTests {

    @Mock
    private AtmCashService atmCashService;
    @InjectMocks
    private AtmCashController atmCashController;

    private int atmId;
    List<AtmCashDepositRequestDTO> depositCashRequest;
    AtmCashWithdrawalRequestDTO withdrawalRequest;

    @BeforeEach
    public void setUp() {
        atmId = 1;
        depositCashRequest = new ArrayList<>();
        withdrawalRequest = new AtmCashWithdrawalRequestDTO(10);
    }

    @Test
    public void addCash_shouldReturn200_whenDepositIsSuccessful() {
        AtmCashDepositResponseDTO successResponse = new AtmCashDepositResponseDTO(
                ResponseConstants.DEPOSIT_SUCCEEDED.getStatus(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getCode(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getMessage());

        when(atmCashService.addCash(depositCashRequest, atmId)).thenReturn(successResponse);
        ResponseEntity<AtmCashDepositResponseDTO> expectedResult = new ResponseEntity<>(successResponse, HttpStatus.OK);

        assertEquals(expectedResult, atmCashController.addCash(depositCashRequest, atmId));
    }

    @Test
    public void addCash_shouldThrowDuplicateBillValuesException_whenThereAreDuplicatesInRequest() {

        when(atmCashService.addCash(depositCashRequest, atmId)).thenThrow(new DuplicateBillValuesException());

        assertThrows(DuplicateBillValuesException.class, () -> atmCashController.addCash(depositCashRequest, atmId));
    }

    @Test
    public void addCash_shouldThrowAtmCapacityExceededException_whenRequestExceedsAtmCapacity() {

        when(atmCashService.addCash(depositCashRequest, atmId)).thenThrow(new AtmCapacityExceededException());

        assertThrows(AtmCapacityExceededException.class, () -> atmCashController.addCash(depositCashRequest, atmId));
    }

    @Test
    public void addCash_shouldThrowRequestSizeExceededException_whenRequestSizeIsTooLarge() {

        when(atmCashService.addCash(depositCashRequest, atmId)).thenThrow(new RequestSizeExceededException());

        assertThrows(RequestSizeExceededException.class, () -> atmCashController.addCash(depositCashRequest, atmId));
    }

    @Test
    public void withdrawCash_shouldThrowAtmNotFoundException_whenUsingNonExistingAtm() {

        when(atmCashService.withdrawCash(withdrawalRequest, atmId)).thenThrow(new AtmNotFoundException());

        assertThrows(AtmNotFoundException.class, () -> atmCashController.withdrawCash(withdrawalRequest, atmId));
    }

    @Test
    public void withdrawCash_shoulThrowNegativeAmountException_whenAmountIsBelowZero() {

        when(atmCashService.withdrawCash(withdrawalRequest, atmId)).thenThrow(new NegativeAmountException());

        assertThrows(NegativeAmountException.class, () -> atmCashController.withdrawCash(withdrawalRequest, atmId));
    }

    @Test
    public void withdrawCash_shoulThrowAmountExceedsAtmCashException_whenAmountIsTooBig() {

        when(atmCashService.withdrawCash(withdrawalRequest, atmId)).thenThrow(new AmountExceedsAtmCashException());

        assertThrows(AmountExceedsAtmCashException.class, () -> atmCashController.withdrawCash(withdrawalRequest, atmId));
    }

    @Test
    public void withdrawCash_shoulThrowImpossibleBillCombinationException_whenAmountCannotBeComputed() {

        when(atmCashService.withdrawCash(withdrawalRequest, atmId)).thenThrow(new ImpossibleBillCombinationException());

        assertThrows(ImpossibleBillCombinationException.class, () -> atmCashController.withdrawCash(withdrawalRequest, atmId));
    }

    @Test
    public void withdrawCash_shouldReturnSuccessfulResponse_whenAmountCanBeComputed() {

        when(atmCashService.withdrawCash(withdrawalRequest, atmId)).thenReturn(new TreeMap<>());

        ResponseEntity<?> actualResponse = atmCashController.withdrawCash(withdrawalRequest, atmId);

        assertEquals(new TreeMap<>(), actualResponse.getBody());
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
    }
}
