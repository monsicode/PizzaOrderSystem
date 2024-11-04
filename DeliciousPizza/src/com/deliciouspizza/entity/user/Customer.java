package com.deliciouspizza.entity.user;

public class Customer extends User {

    private final String address;

    public Customer(String username, String password, String address) {
        super(username, password);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

}
