package com.deliciouspizza.ui;

public interface CustomerInterface extends UserInterface {
    void createOrder(String username);

    void viewHistoryOfOrders(String username);

    void viewProductMenu();
}