package com.atm.management.service;

import com.atm.management.constants.ResponseConstants;
import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashDepositResponseDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.exception.*;
import com.atm.management.model.Atm;
import com.atm.management.model.AtmCash;
import com.atm.management.repository.AtmCashDAO;
import com.atm.management.repository.AtmDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

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
    private AtmCashDepositResponseDTO depositSuccessResponse;
    List<AtmCashDepositRequestDTO> depositRequestList;

    @BeforeEach
    public void setUp() {

        atmId = 1;
        billValue = 10;
        atm = new Atm(atmId, "Virtutii", "12A", "Bucuresti", "Romania");
        depositRequestList = new ArrayList<>();
        depositSuccessResponse = new AtmCashDepositResponseDTO(
                ResponseConstants.DEPOSIT_SUCCEEDED.getStatus(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getCode(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getMessage());
    }

    // Deposit cash tests
    @Test
    public void addCash_shouldReturnSuccess_whenUsingValidRequest_withNewBillValue() {

        List<AtmCashDepositRequestDTO> requestList = new ArrayList<>();
        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 10);
        requestList.add(request);

        Mockito.when(atmDAO.getById(atmId)).thenReturn(atm);
        Mockito.when(atmCashDAO.findByBillValueAndAtmId(billValue, atmId)).thenReturn(null);
        Mockito.when(atmCashDAO.save(any(AtmCash.class))).thenReturn(any(AtmCash.class));

        AtmCashDepositResponseDTO actualResponse = atmCashService.addCash(requestList, atmId);
        assertEquals(actualResponse, depositSuccessResponse);

    }

    @Test
    public void addCash_shouldReturnSuccess_whenUsingValidRequest_withAlreadyInsertedBillValue() {

        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 10);
        depositRequestList.add(request);
        AtmCash existingBill = new AtmCash(atm, billValue, 15);

        Mockito.when(atmDAO.getById(atmId)).thenReturn(atm);
        Mockito.when(atmCashDAO.findByBillValueAndAtmId(billValue, atmId)).thenReturn(existingBill);
        Mockito.when(atmCashDAO.save(any(AtmCash.class))).thenReturn(any(AtmCash.class));

        AtmCashDepositResponseDTO actualResponse = atmCashService.addCash(depositRequestList, atmId);
        assertEquals(actualResponse, depositSuccessResponse);

    }

    @Test
    public void addCash_shouldThrowDuplicateBillValuesException_whenRequestContainsDuplicates() {

        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 10);
        depositRequestList.add(request);
        depositRequestList.add(request);

        assertThrows(DuplicateBillValuesException.class, () -> atmCashService.addCash(depositRequestList, atmId));
    }

    @Test
    public void addCash_shouldThrowAtmCapacityExceededException_whenRequestExceedsAtmCapacity() {

        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 1000000);
        depositRequestList.add(request);

        assertThrows(AtmCapacityExceededException.class, () -> atmCashService.addCash(depositRequestList, atmId));
    }

    @Test
    public void addCash_shouldThrowAtmNotFoundException_whenUsingNonExistingAtm() {

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.empty());
        assertThrows(AtmNotFoundException.class, () -> atmCashService.addCash(null, atmId));
    }

    // Withdraw cash tests
    @Test
    public void withdrawCash_shouldThrowAtmNotFoundException_whenUsingNonExistingAtm() {

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.empty());
        assertThrows(AtmNotFoundException.class, () -> atmCashService.withdrawCash(null, atmId));
    }

    @Test
    public void withdrawCash_shouldThrowNegativeAmountException_whenAmountIsBelowZero() {

        AtmCashWithdrawalRequestDTO requestDTO = new AtmCashWithdrawalRequestDTO(-1);

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));

        assertThrows(NegativeAmountException.class, () -> atmCashService.withdrawCash(requestDTO, atmId));
    }

    @Test
    public void withdrawCash_shouldThrowNegativeAmountException_whenAmountIsZero() {

        AtmCashWithdrawalRequestDTO requestDTO = new AtmCashWithdrawalRequestDTO(0);

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));

        assertThrows(NegativeAmountException.class, () -> atmCashService.withdrawCash(requestDTO, atmId));
    }

    @Test
    public void withdrawCash_shouldThrowAmountExceedsAtmCashException_whenAmountIsTooBig() {

        AtmCashWithdrawalRequestDTO requestDTO = new AtmCashWithdrawalRequestDTO(1000);
        List<AtmCash> atmCash = new ArrayList<>();
        atmCash.add(new AtmCash(atm, billValue, 5));

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));
        Mockito.when(atmCashDAO.findByAtmId(atmId)).thenReturn(atmCash);

        assertThrows(AmountExceedsAtmCashException.class, () -> atmCashService.withdrawCash(requestDTO, atmId));

    }

    @Test
    public void withdrawCash_shouldThrowImpossibleBillCombinationException_whenAmountCannotBeComputed(){

        AtmCashWithdrawalRequestDTO requestDTO = new AtmCashWithdrawalRequestDTO(13);
        List<AtmCash> atmCash = new ArrayList<>();
        atmCash.add(new AtmCash(atm, billValue, 5));

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));
        Mockito.when(atmCashDAO.findByAtmId(atmId)).thenReturn(atmCash);

        assertThrows(ImpossibleBillCombinationException.class, () -> atmCashService.withdrawCash(requestDTO, atmId));
    }

    @Test
    public void withdrawCash_shouldReturnSuccessfulResponse_whenAmountCanBeComputed(){

        AtmCashWithdrawalRequestDTO requestDTO = new AtmCashWithdrawalRequestDTO(120);
        AtmCash tenBill = new AtmCash(atm, 10, 50);
        AtmCash hundredBill = new AtmCash(atm, 100, 20);

        List<AtmCash> atmCash = new ArrayList<>();
        atmCash.add(hundredBill);
        atmCash.add(tenBill);

        Map<Integer, Integer> expectedResponse = new TreeMap<>();
        expectedResponse.put(100, 1);
        expectedResponse.put(10,2);

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));
        Mockito.when(atmCashDAO.findByAtmId(atmId)).thenReturn(atmCash);
        Mockito.when(atmCashDAO.findByBillValueAndAtmId(10, atmId)).thenReturn(tenBill);
        Mockito.when(atmCashDAO.findByBillValueAndAtmId(100, atmId)).thenReturn(hundredBill);
        Mockito.when(atmCashDAO.save(tenBill)).thenReturn(tenBill);
        Mockito.when(atmCashDAO.save(hundredBill)).thenReturn(hundredBill);

        assertEquals(expectedResponse, atmCashService.withdrawCash(requestDTO, atmId));
    }



}
