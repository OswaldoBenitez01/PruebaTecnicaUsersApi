package com.OswaldoBenitez.usersApi.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class User {

    private String id;
    private String email;
    private String name;
    private String phone;

    @JsonIgnore
    private String password;

    private String taxId;
    private String createdAt;
    private List<Address> addresses;

    public User() {
    }

    public User(String id, String email, String name, String phone, String password, String taxId, String createdAt, List<Address> addresses) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.taxId = taxId;
        this.createdAt = createdAt;
        this.addresses = addresses;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
