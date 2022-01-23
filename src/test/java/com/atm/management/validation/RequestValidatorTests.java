package com.atm.management.validation;

import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.exception.*;
import com.atm.management.model.Atm;
import com.atm.management.model.AtmCash;
import com.atm.management.repository.AtmCashDAO;
import com.atm.management.repository.AtmDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class RequestValidatorTests {

    @Mock
    private AtmCashDAO atmCashDAO;
    @Mock
    private AtmDAO atmDAO;
    @InjectMocks
    private RequestValidatorImpl requestValidator;

    private int atmId;
    int billValue;
    private Atm atm;
    List<AtmCashDepositRequestDTO> depositRequestList;

    @BeforeEach
    public void setUp() {

        atmId = 1;
        billValue = 10;
        atm = new Atm(atmId, "Virtutii", "12A", "Bucuresti", "Romania");
        depositRequestList = new ArrayList<>();
    }

    // Deposit cash tests
    @Test
    public void validateDepositRequest_shouldNotThrowError_whenUsingValidRequest() {

        //construct request
        List<AtmCashDepositRequestDTO> requestList = new ArrayList<>();
        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 10);
        requestList.add(request);

        //construct atm cash
        List<AtmCash> atmTotalCash = new ArrayList<>();
        AtmCash atmCash = new AtmCash(atm, billValue, 50);
        atmTotalCash.add(atmCash);

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));
        Mockito.when(atmCashDAO.findByAtmId(atmId)).thenReturn(atmTotalCash);

        requestValidator.validateDepositRequest(requestList, atmId);
    }

    @Test
    public void validateDepositRequest_shouldThrowDuplicateBillValuesException_whenRequestContainsDuplicates() {

        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 10);
        depositRequestList.add(request);
        depositRequestList.add(request);

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));

        assertThrows(DuplicateBillValuesException.class, () -> requestValidator.validateDepositRequest(depositRequestList, atmId));
    }

    @Test
    public void validateDepositRequest_shouldThrowAtmCapacityExceededException_whenRequestExceedsAtmCapacity() {

        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 1000000);
        depositRequestList.add(request);

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));

        assertThrows(AtmCapacityExceededException.class, () -> requestValidator.validateDepositRequest(depositRequestList, atmId));
    }

    @Test
    public void validateDepositRequest_shouldThrowAtmNotFoundException_whenUsingNonExistingAtm() {

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.empty());
        assertThrows(AtmNotFoundException.class, () -> requestValidator.validateDepositRequest(null, atmId));
    }

    @Test
    public void validateDepositRequest_shouldThrowRequestSizeExceededException_whenRequestIsTooLarge() {

        AtmCashDepositRequestDTO request = new AtmCashDepositRequestDTO(billValue, 10);
        for (int i = 0; i < 101; i++) {
            depositRequestList.add(request);
        }

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));

        assertThrows(RequestSizeExceededException.class, () -> requestValidator.validateDepositRequest(depositRequestList, atmId));
    }

    // Withdraw cash tests
    @Test
    public void validateWithdrawalRequest_shouldThrowAtmNotFoundException_whenUsingNonExistingAtm() {

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.empty());
        assertThrows(AtmNotFoundException.class, () -> requestValidator.validateWithdrawalRequest(null, atmId));
    }

    @Test
    public void validateWithdrawalRequest_shouldThrowNegativeAmountException_whenAmountIsBelowZero() {

        AtmCashWithdrawalRequestDTO requestDTO = new AtmCashWithdrawalRequestDTO(-1);

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));

        assertThrows(NegativeAmountException.class, () -> requestValidator.validateWithdrawalRequest(requestDTO, atmId));
    }

    @Test
    public void withdrawCash_shouldThrowNegativeAmountException_whenAmountIsZero() {

        AtmCashWithdrawalRequestDTO requestDTO = new AtmCashWithdrawalRequestDTO(0);

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));

        assertThrows(NegativeAmountException.class, () -> requestValidator.validateWithdrawalRequest(requestDTO, atmId));
    }

    @Test
    public void withdrawCash_shouldThrowAmountExceedsAtmCashException_whenAmountIsTooBig() {

        AtmCashWithdrawalRequestDTO requestDTO = new AtmCashWithdrawalRequestDTO(1000);
        List<AtmCash> atmCash = new ArrayList<>();
        atmCash.add(new AtmCash(atm, billValue, 5));

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));
        Mockito.when(atmCashDAO.findByAtmId(atmId)).thenReturn(atmCash);

        assertThrows(AmountExceedsAtmCashException.class, () -> requestValidator.validateWithdrawalRequest(requestDTO, atmId));

    }

    @Test
    public void withdrawCash_shouldNotThrowAnyException_whenRequestIsValid(){

        AtmCashWithdrawalRequestDTO requestDTO = new AtmCashWithdrawalRequestDTO(10);
        List<AtmCash> atmCash = new ArrayList<>();
        atmCash.add(new AtmCash(atm, billValue, 1));

        Mockito.when(atmDAO.findById(atmId)).thenReturn(Optional.ofNullable(atm));
        Mockito.when(atmCashDAO.findByAtmId(atmId)).thenReturn(atmCash);

        requestValidator.validateWithdrawalRequest(requestDTO, atmId);
    }

}
