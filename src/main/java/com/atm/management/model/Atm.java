package com.atm.management.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "atm")
public class Atm implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private String street;
    private String number;
    private String city;
    private String country;

    @OneToMany(mappedBy="atm")
    private Set<AtmCash> cash;

    public Atm(Integer id, String street, String number, String city, String country) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.city = city;
        this.country = country;
    }

    public Atm() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<AtmCash> getCash() {
        return cash;
    }

    public void setCash(Set<AtmCash> cash) {
        this.cash = cash;
    }
}
