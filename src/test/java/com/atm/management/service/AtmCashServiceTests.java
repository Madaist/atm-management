package com.atm.management.service;

import com.atm.management.constants.ResponseConstants;
import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashDepositResponseDTO;
import com.atm.management.exception.AtmCapacityExceededException;
import com.atm.management.exception.DuplicateBillValuesException;
import com.atm.management.model.Atm;
import com.atm.management.model.AtmCash;
import com.atm.management.repository.AtmCashDAO;
import com.atm.management.repository.AtmDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
public class AtmCashServiceTests {

    @Mock
    private AtmCashDAO atmCashDAO;
    @Mock
    private AtmDAO atmDAO;
    @Spy
    private ModelMapper modelMapper;
    @InjectMocks
    private AtmCashServiceImpl atmCashService;

    private int atmId;
    int billValue;
    private Atm atm;
    private AtmCashDepositResponseDTO successResponse;
    List<AtmCashDepositRequestDTO> requestList;

    @BeforeEach
    public void setUp() {

        atmId = 1;
        billValue = 10;
        atm = new Atm(atmId, "Virtutii", "12A", "Bucuresti", "Romania");
        requestList = new ArrayList<>();
        successResponse = new AtmCashDepositResponseDTO(
                ResponseConstants.DEPOSIT_SUCCEEDED.getStatus(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getCode(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getMessage());
    }

    @Test
    public void addCash_shouldReturnSuccess_whenUsingValidRequest_withNewBillValue() {

        List<AtmCashDepositRequestDTO> requestList = new ArrayList<>();
        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 10);
        requestList.add(request);

        Mockito.when(atmDAO.getById(atmId)).thenReturn(atm);
        Mockito.when(atmCashDAO.findByBillValueAndAtmId(billValue, atmId)).thenReturn(null);
        Mockito.when(atmCashDAO.save(any(AtmCash.class))).thenReturn(any(AtmCash.class));

        AtmCashDepositResponseDTO actualResponse = atmCashService.addCash(requestList, atmId);
        assertEquals(actualResponse, successResponse);

    }

    @Test
    public void addCash_shouldReturnSuccess_whenUsingValidRequest_withAlreadyInsertedBillValue() {

        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 10);
        requestList.add(request);
        AtmCash existingBill = new AtmCash(atm, billValue, 15);

        Mockito.when(atmDAO.getById(atmId)).thenReturn(atm);
        Mockito.when(atmCashDAO.findByBillValueAndAtmId(billValue, atmId)).thenReturn(existingBill);
        Mockito.when(atmCashDAO.save(any(AtmCash.class))).thenReturn(any(AtmCash.class));

        AtmCashDepositResponseDTO actualResponse = atmCashService.addCash(requestList, atmId);
        assertEquals(actualResponse, successResponse);

    }

    @Test
    public void addCash_shouldThrowDuplicateBillValuesException_whenRequestContainsDuplicates() {

        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 10);
        requestList.add(request);
        requestList.add(request);

        assertThrows(DuplicateBillValuesException.class, () -> atmCashService.addCash(requestList, atmId));
    }

    @Test
    public void addCash_shouldThrowAtmCapacityExceededException_whenRequestExceedsAtmCapacity() {

        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 1000000);
        requestList.add(request);

        assertThrows(AtmCapacityExceededException.class, () -> atmCashService.addCash(requestList, atmId));
    }


}
