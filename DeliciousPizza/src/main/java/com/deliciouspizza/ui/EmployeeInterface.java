package com.deliciouspizza.ui;

public interface EmployeeInterface extends UserInterface {
    void processOrder();

    void viewPendingOrders();

    void viewCompletedOrders();
}
