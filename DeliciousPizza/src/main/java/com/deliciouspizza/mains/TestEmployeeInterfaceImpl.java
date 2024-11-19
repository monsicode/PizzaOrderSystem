package com.deliciouspizza.mains;

import com.deliciouspizza.ui.EmployeeInterfaceImpl;
import com.deliciouspizza.ui.UserInterface;

import java.util.Scanner;

public class TestEmployeeInterfaceImpl {
    //    private static EmployeeInterfaceImpl currentUserInterface;
    private static UserInterface currentUserInterface;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        currentUserInterface = new EmployeeInterfaceImpl(scanner);
        boolean running = true;

        while (running) {
//            currentUserInterface.showMainMenuEmployee("worker1");
            currentUserInterface.displayMenu();
        }

        scanner.close();

    }
}
