package com.atm.management.controller;

import com.atm.management.constants.ResponseConstants;
import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashDepositResponseDTO;
import com.atm.management.exception.AtmCapacityExceededException;
import com.atm.management.exception.DuplicateBillValuesException;
import com.atm.management.exception.RequestSizeExceededException;
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
    List<AtmCashDepositRequestDTO> request;

    @BeforeEach
    public void setUp() {
        atmId = 1;
        request = new ArrayList<>();

    }

    @Test
    public void addCash_shouldReturn200_whenDepositIsSuccessful() {
        AtmCashDepositResponseDTO successResponse = new AtmCashDepositResponseDTO(
                ResponseConstants.DEPOSIT_SUCCEEDED.getStatus(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getCode(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getMessage());

        when(atmCashService.addCash(request, atmId)).thenReturn(successResponse);
        ResponseEntity<AtmCashDepositResponseDTO> expectedResult = new ResponseEntity<>(successResponse, HttpStatus.OK);

        assertEquals(expectedResult, atmCashController.addCash(request, atmId));
    }

    @Test
    public void addCash_shouldThrowDuplicateBillValuesException_whenThereAreDuplicatesInRequest() {

        when(atmCashService.addCash(request, atmId)).thenThrow(new DuplicateBillValuesException());

        assertThrows(DuplicateBillValuesException.class, () -> atmCashController.addCash(request, atmId));
    }

    @Test
    public void addCash_shouldThrowAtmCapacityExceededException_whenRequestExceedsAtmCapacity() {

        when(atmCashService.addCash(request, atmId)).thenThrow(new AtmCapacityExceededException());

        assertThrows(AtmCapacityExceededException.class, () -> atmCashController.addCash(request, atmId));
    }

    @Test
    public void addCash_shouldThrowRequestSizeExceededException_whenRequestSizeIsTooLarge() {

        when(atmCashService.addCash(request, atmId)).thenThrow(new RequestSizeExceededException());

        assertThrows(RequestSizeExceededException.class, () -> atmCashController.addCash(request, atmId));
    }


}
