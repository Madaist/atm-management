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

    @ManyToOne
    @JoinColumn(name = "atm_id", nullable = false)
    private Atm atm;

    @Column(name = "bill_value")
    @BillValue
    private int billValue;

    @Column(name = "bill_count")
    @Min(1)
    @Max(100000)
    @NotNull
    private int billCount;

    public AtmCash() {
    }

    public AtmCash(Atm atm, int billValue, int billCount) {
        this.atm = atm;
        this.billValue = billValue;
        this.billCount = billCount;
    }

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

    public Atm getAtm() {
        return atm;
    }

    public void setAtm(Atm atm) {
        this.atm = atm;
    }
}
