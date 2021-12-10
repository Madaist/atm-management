package com.atm.management.repository;

import com.atm.management.model.Atm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtmDAO extends JpaRepository<Atm, Integer>  {

}
