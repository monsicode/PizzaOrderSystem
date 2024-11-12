package com.deliciouspizza.entity.user;

public class Customer extends User {

    private String address;

    public Customer(String username, String password, String address) {
        super(username, password);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
