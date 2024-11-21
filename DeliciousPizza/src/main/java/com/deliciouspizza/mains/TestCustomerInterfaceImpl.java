package com.deliciouspizza.mains;

import com.deliciouspizza.ui.CustomerInterfaceImpl;

import java.util.Scanner;

public class TestCustomerInterfaceImpl {
    private static CustomerInterfaceImpl currentUserInterface;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        currentUserInterface = new CustomerInterfaceImpl(scanner);
        boolean running = true;

        while (running) {

            currentUserInterface.showMainMenuCustomer("monkata");
        }

        scanner.close();

    }

}
