package com.atm.management.controller;

import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.dto.ApiResponse;
import com.atm.management.service.AtmCashService;
import com.atm.management.validation.RequestValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/atm")
public class AtmCashController {

    private final RequestValidator requestValidator;
    private final AtmCashService atmCashService;

    public AtmCashController(RequestValidator requestValidator, AtmCashService atmCashService) {
        this.requestValidator = requestValidator;
        this.atmCashService = atmCashService;
    }

    @PostMapping(value = "/{id}/deposit")
    public ResponseEntity<ApiResponse> addCash(@Valid @RequestBody List<AtmCashDepositRequestDTO> request,
                                               @PathVariable int id) {

        requestValidator.validateDepositRequest(request, id);
        ApiResponse response = atmCashService.addCash(request, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/withdraw")
    public ResponseEntity<?> withdrawCash(@RequestBody AtmCashWithdrawalRequestDTO request,
                                          @PathVariable int id) {

        requestValidator.validateWithdrawalRequest(request, id);
        Map<Integer, Integer> response = atmCashService.withdrawCash(request, id);
        return new ResponseEntity<>(response,  HttpStatus.OK);
    }
}
