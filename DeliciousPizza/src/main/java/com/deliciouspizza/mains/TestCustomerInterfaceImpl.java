package com.deliciouspizza.mains;

import com.deliciouspizza.ui.CustomerInterfaceImpl;
import com.deliciouspizza.ui.UserInterface;

import java.util.Scanner;

public class TestCustomerInterfaceImpl {
    private static UserInterface currentUserInterface;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        currentUserInterface = new CustomerInterfaceImpl(scanner);
        boolean running = true;

        while (running) {

            currentUserInterface.displayMenu();

        }

        scanner.close();

    }

}
