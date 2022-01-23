package com.atm.management.controller;

import com.atm.management.constants.ResponseConstants;
import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.ApiResponse;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.exception.*;
import com.atm.management.service.AtmCashService;
import com.atm.management.validation.RequestValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AtmCashController.class)
public class AtmCashControllerTests {

    private static final String CASH_CONTROLLER_DEPOSIT_ENDPOINT = "/api/v1/atm/1/deposit";
    private static final String CASH_CONTROLLER_WITHDRAW_ENDPOINT = "/api/v1/atm/1/withdraw";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AtmCashService atmCashService;

    @MockBean
    private RequestValidator requestValidator;

    @Autowired
    private ObjectMapper objectMapper;


    private int atmId;
    List<AtmCashDepositRequestDTO> depositCashRequest;
    AtmCashWithdrawalRequestDTO withdrawalRequest;

    @BeforeEach
    public void setUp() {
        atmId = 1;
        depositCashRequest = new ArrayList<>();
        AtmCashDepositRequestDTO atmCashDepositRequestDTO = new AtmCashDepositRequestDTO(10, 1);
        depositCashRequest.add(atmCashDepositRequestDTO);
        withdrawalRequest = new AtmCashWithdrawalRequestDTO(10);
    }

    @Test
    public void addCash_shouldReturn200_whenDepositIsSuccessful() throws Exception {
        ApiResponse successResponse = new ApiResponse(
                ResponseConstants.DEPOSIT_SUCCEEDED.getStatus(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getCode(),
                ResponseConstants.DEPOSIT_SUCCEEDED.getMessage());

        doNothing().when(requestValidator).validateDepositRequest(depositCashRequest, atmId);
        when(atmCashService.addCash(depositCashRequest, atmId)).thenReturn(successResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(CASH_CONTROLLER_DEPOSIT_ENDPOINT)
                        .content(asJsonString(depositCashRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestValidator, times(1)).validateDepositRequest(depositCashRequest, atmId);
        verify(atmCashService, times(1)).addCash(depositCashRequest, atmId);
    }

    @Test
    public void addCash_shouldReturn400_whenThereAreDuplicatesInRequest() throws Exception {

        doThrow(new DuplicateBillValuesException()).when(requestValidator).validateDepositRequest(depositCashRequest, atmId);

        ApiResponse response = processResponse(
                mockMvc.perform(MockMvcRequestBuilders
                                .post(CASH_CONTROLLER_DEPOSIT_ENDPOINT)
                                .content(asJsonString(depositCashRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().is4xxClientError())
                        .andReturn(), objectMapper);

        assertEquals("The request contains duplicate bill values.", response.getMessage());
        verify(requestValidator, times(1)).validateDepositRequest(depositCashRequest, atmId);
    }

    @Test
    public void addCash_shouldReturn400_whenRequestExceedsAtmCapacity() throws Exception {

        doThrow(new AtmCapacityExceededException()).when(requestValidator).validateDepositRequest(depositCashRequest, atmId);

        ApiResponse response = processResponse(
                mockMvc.perform(MockMvcRequestBuilders
                                .post(CASH_CONTROLLER_DEPOSIT_ENDPOINT)
                                .content(asJsonString(depositCashRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().is4xxClientError())
                        .andReturn(), objectMapper);

        assertEquals("The atm has a capacity of 100.000 bills", response.getMessage());
        verify(requestValidator, times(1)).validateDepositRequest(depositCashRequest, atmId);
    }

    @Test
    public void addCash_shouldReturn400_whenRequestSizeIsTooLarge() throws Exception {

        doThrow(new RequestSizeExceededException()).when(requestValidator).validateDepositRequest(depositCashRequest, atmId);

        ApiResponse response = processResponse(mockMvc.perform(MockMvcRequestBuilders
                        .post(CASH_CONTROLLER_DEPOSIT_ENDPOINT)
                        .content(asJsonString(depositCashRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn(), objectMapper);

        assertEquals("The size of the request is too large. Please, introduce a number of pairs smaller than 100",
                response.getMessage());
        verify(requestValidator, times(1)).validateDepositRequest(depositCashRequest, atmId);
    }

    @Test
    public void addCash_shouldReturn404_whenUsingNonExistingAtm() throws Exception {

        doThrow(new AtmNotFoundException()).when(requestValidator).validateDepositRequest(depositCashRequest, atmId);

        ApiResponse response = processResponse(mockMvc.perform(MockMvcRequestBuilders
                        .post(CASH_CONTROLLER_DEPOSIT_ENDPOINT)
                        .content(asJsonString(depositCashRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn(), objectMapper);

        assertEquals("The atm doesn't exist", response.getMessage());
        verify(requestValidator, times(1)).validateDepositRequest(depositCashRequest, atmId);
    }

    @Test
    public void withdrawCash_shouldReturn400_whenAmountIsBelowZero() throws Exception {

        doThrow(new NegativeAmountException()).when(requestValidator).validateWithdrawalRequest(withdrawalRequest, atmId);

        ApiResponse response = processResponse(mockMvc.perform(MockMvcRequestBuilders
                        .post(CASH_CONTROLLER_WITHDRAW_ENDPOINT)
                        .content(asJsonString(withdrawalRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn(), objectMapper);

        assertEquals("Requested amount can not be smaller than or equal to zero. Please, insert a positive number",
                response.getMessage());
        verify(requestValidator, times(1)).validateWithdrawalRequest(withdrawalRequest, atmId);
    }

    @Test
    public void withdrawCash_shouldReturn404_whenAmountIsTooBig() throws Exception {

        doThrow(new AmountExceedsAtmCashException()).when(requestValidator).validateWithdrawalRequest(withdrawalRequest, atmId);

        ApiResponse response = processResponse(mockMvc.perform(MockMvcRequestBuilders
                        .post(CASH_CONTROLLER_WITHDRAW_ENDPOINT)
                        .content(asJsonString(withdrawalRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn(), objectMapper);

        assertEquals("The requested amount doesn't exist in the ATM at the moment. Please, try to withdraw a smaller amount.",
                response.getMessage());
        verify(requestValidator, times(1)).validateWithdrawalRequest(withdrawalRequest, atmId);
    }

    @Test
    public void withdrawCash_shouldReturn400_whenAmountCannotBeComputed() throws Exception {

        doNothing().when(requestValidator).validateWithdrawalRequest(withdrawalRequest, atmId);
        when(atmCashService.withdrawCash(withdrawalRequest, atmId)).thenThrow(new ImpossibleBillCombinationException());

        ApiResponse response = processResponse(mockMvc.perform(MockMvcRequestBuilders
                        .post(CASH_CONTROLLER_WITHDRAW_ENDPOINT)
                        .content(asJsonString(withdrawalRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn(), objectMapper);

        assertEquals("Requested amount can not be computed", response.getMessage());
        verify(requestValidator, times(1)).validateWithdrawalRequest(withdrawalRequest, atmId);
    }

    @Test
    public void withdrawCash_shouldReturn200_whenAmountCanBeComputed() throws Exception {

        doNothing().when(requestValidator).validateWithdrawalRequest(withdrawalRequest, atmId);
        when(atmCashService.withdrawCash(withdrawalRequest, atmId)).thenReturn(new TreeMap<>());

        mockMvc.perform(MockMvcRequestBuilders
                        .post(CASH_CONTROLLER_WITHDRAW_ENDPOINT)
                        .content(asJsonString(withdrawalRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(requestValidator, times(1)).validateWithdrawalRequest(withdrawalRequest, atmId);
        verify(atmCashService, times(1)).withdrawCash(withdrawalRequest, atmId);
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ApiResponse processResponse(MvcResult mvcResult, ObjectMapper objectMapper) throws Exception {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class);
    }
}