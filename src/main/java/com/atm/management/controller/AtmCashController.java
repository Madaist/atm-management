package com.atm.management.controller;

import com.atm.management.dto.AtmCashDepositRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalRequestDTO;
import com.atm.management.dto.AtmCashWithdrawalResponseDTO;
import com.atm.management.dto.AtmCashDepositResponseDTO;
import com.atm.management.service.AtmCashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/atm")
public class AtmCashController {

    private final AtmCashService atmCashService;

    public AtmCashController(AtmCashService atmCashService) {
        this.atmCashService = atmCashService;
    }

    @PostMapping(value = "/{id}/deposit")
    public ResponseEntity<AtmCashDepositResponseDTO> addCash(@Valid @RequestBody List<AtmCashDepositRequestDTO> request,
                                                             @PathVariable int id) {

        AtmCashDepositResponseDTO response = atmCashService.addCash(request, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/withdraw")
    public ResponseEntity<AtmCashWithdrawalResponseDTO> withdrawCash(@Valid @RequestBody List<AtmCashWithdrawalRequestDTO> request,
                                                                     @PathVariable int id) {

        AtmCashWithdrawalResponseDTO response = atmCashService.withdrawCash(request, id);
        return new ResponseEntity<>( HttpStatus.OK);
    }
}
