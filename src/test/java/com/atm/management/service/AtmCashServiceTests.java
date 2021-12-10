package com.atm.management.service;


import com.atm.management.repository.IAtmCashDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class AtmCashServiceTests {

    @Mock
    private IAtmCashDAO atmCashDAO;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private AtmCashServiceImpl atmCashService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void addCash_shouldReturnSuccess_whenUsingValidRequest(){
    }


}
