package com.deliciouspizza.mains;

import com.deliciouspizza.service.UserService;
import com.deliciouspizza.ui.CustomerInterfaceImpl;
import com.deliciouspizza.ui.EmployeeInterfaceImpl;
import com.deliciouspizza.ui.UserInterface;

import java.util.Scanner;

public class TestEmployeeInterfaceImpl {
    private static EmployeeInterfaceImpl currentUserInterface;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        currentUserInterface = new EmployeeInterfaceImpl(scanner);
        boolean running = true;

        while (running) {
            currentUserInterface.showMainMenuEmployee("worker1");
        }

        scanner.close();

    }

//    private static void handleLogin(Scanner scanner, UserService userService) {
//        System.out.print("Enter username: ");
//        String username = scanner.nextLine();
//        System.out.print("Enter password: ");
//        String password = scanner.nextLine();
//
//        String role = userService.loginUser(username, password);
//
//        if (role == null) {
//            System.out.println("Invalid username or password. Try again.");
//        } else {
//            UserInterface userInterface = role.equalsIgnoreCase("customer")
//                ? new CustomerInterfaceImpl(scanner)
//                : new EmployeeInterfaceImpl(scanner);
//
//            // Преминаваме към съответния интерфейс
//            userInterface.handleLogin(); // Вече съществуваща имплементация.
//            userInterface.displayMenu();
//        }
//    }
//
//    {
//        switch (choice) {
//            case 1 -> {
//                UserInterface registrationInterface = new CustomerInterfaceImpl(scanner);
//                registrationInterface.handleRegistration(); // Или аналогично EmployeeInterfaceImpl
//            }
//            case 2 -> handleLogin(scanner, userService); // Описано по-горе.
//            case 3 -> {
//                System.out.println("Goodbye!");
//                scanner.close();
//                System.exit(0);
//            }
//            default -> System.out.println("Invalid option. Please try again.");
//        }
//
//    }


}
