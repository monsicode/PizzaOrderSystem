package com.deliciouspizza.mains;

import com.deliciouspizza.ui.CustomerInterfaceImpl;
import com.deliciouspizza.ui.UserInterface;

import java.util.Scanner;

public class TestCustomerInterfaceImpl {
    private static CustomerInterfaceImpl currentUserInterface;
//    private static UserInterface currentUserInterface;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        currentUserInterface = new CustomerInterfaceImpl(scanner);
        boolean running = true;

        while (running) {
            System.out.println("Enter username");
            String username = scanner.nextLine();
        //    currentUserInterface.displayMenu();

            currentUserInterface.showOrderMenu(username);

        }

        scanner.close();

    }

}
