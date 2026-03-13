package com.OswaldoBenitez.usersApi.Model;

public class Address {

    private Integer id;
    private String name;
    private String street;
    private String countryCode;

    public Address() {
    }

    public Address(Integer id, String name, String street, String countryCode) {
        this.id = id;
        this.name = name;
        this.street = street;
        this.countryCode = countryCode;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
