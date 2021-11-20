package com.atm.management.model;

import com.atm.management.validation.BillValue;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "atm_cash")
public class AtmCash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "atm_id")
    private Integer atmId;

    @Column(name = "bill_value")
    @BillValue
    private int billValue;

    @Column(name = "bill_count")
    @Min(1)
    @Max(100000)
    @NotNull
    private int billCount;

    public int getBillValue() {
        return billValue;
    }

    public void setBillValue(int billValue) {
        this.billValue = billValue;
    }

    public int getBillCount() {
        return billCount;
    }

    public void setBillCount(int billCount) {
        this.billCount = billCount;
    }

    public Integer getAtmId() {
        return atmId;
    }

    public void setAtmId(Integer atmId) {
        this.atmId = atmId;
    }
}
