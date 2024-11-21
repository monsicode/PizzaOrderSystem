package com.deliciouspizza.mains;

import com.deliciouspizza.ui.EmployeeInterfaceImpl;

import java.util.Scanner;

public class TestEmployeeInterfaceImpl {
    private static EmployeeInterfaceImpl currentUserInterface;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        currentUserInterface = new EmployeeInterfaceImpl(scanner);
        boolean running = true;

        while (running) {
            currentUserInterface.showMainMenuUser("worker1");
        }

        scanner.close();
    }

}

