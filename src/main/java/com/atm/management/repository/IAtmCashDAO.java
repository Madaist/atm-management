package com.atm.management.repository;

import com.atm.management.model.AtmCash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IAtmCashDAO extends JpaRepository<AtmCash, Integer> {

    AtmCash findByBillValueAndAtmId(int billValue, int atmId);
    List<AtmCash> findByAtmId(Integer atmId);

}
