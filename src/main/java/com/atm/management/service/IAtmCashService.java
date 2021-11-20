package com.atm.management.service;

import com.atm.management.dto.AtmCashDTO;
import com.atm.management.exception.AtmCapacityExceededException;
import com.atm.management.exception.DuplicateBillValuesException;
import com.atm.management.exception.RequestSizeExceededException;
import com.atm.management.model.AtmCashDepositResponse;

import java.util.List;

public interface IAtmCashService {

    AtmCashDepositResponse addCash(List<AtmCashDTO> request, int atmId) throws DuplicateBillValuesException, RequestSizeExceededException, AtmCapacityExceededException;
}
