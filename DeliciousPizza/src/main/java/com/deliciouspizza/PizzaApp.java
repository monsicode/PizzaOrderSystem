package com.deliciouspizza;

import com.deliciouspizza.ui.UserInterface;
import com.deliciouspizza.ui.UserInterfaceImpl;

import java.util.Scanner;

public class PizzaApp {
    private static UserInterface currentUserInterface;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        currentUserInterface = new UserInterfaceImpl(scanner);
        boolean running = true;

        while (running) {
            currentUserInterface.displayMenu();
        }

        scanner.close();
    }
}
