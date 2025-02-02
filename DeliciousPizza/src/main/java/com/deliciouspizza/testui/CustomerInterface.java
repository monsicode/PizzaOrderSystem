package com.deliciouspizza.testui;

import com.deliciouspizza.exception.ProductException;

public interface CustomerInterface extends UserInterface {
    void createOrder(String username) throws ProductException;

    void viewHistoryOfOrders(String username);

    void viewProductMenu();
}