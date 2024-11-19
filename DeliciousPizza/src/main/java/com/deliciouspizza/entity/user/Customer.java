package com.deliciouspizza.entity.user;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.enums.UserRights;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@JsonTypeName("customer")
public class Customer extends User {

    private String address;
    private int age;
    protected final Set<Order> orderHistory = new HashSet<>();

    public Customer() {
        super("", "");
        this.userType = "customer"; // Сетваме userType за Customer
    }

    public Customer(String username, String password, String address, int age) {
        super(username, password);
        this.address = address;
        this.age = age;
        rights = UserRights.CUSTOMER;
        this.userType = "customer";
    }

    public String getAddress() {
        return address;
    }

    public int getAge() {
        return age;
    }

    public Set<Order> getOrderHistory() {
        return Collections.unmodifiableSet(orderHistory);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void addOrderToHistory(Order order) {
        orderHistory.add(order);
    }

}
