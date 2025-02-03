package com.deliciouspizza.testui;

import com.deliciouspizza.enums.UserRights;
import com.deliciouspizza.exception.ProductException;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.service.UserService;
import com.deliciouspizza.utils.Singleton;

import java.util.InputMismatchException;
import java.util.Scanner;

public class UserInterfaceImpl implements UserInterface {

    protected static final String RESET = "\u001B[0m";
    protected static final String YELLOW = "\u001B[33m";
    protected static final String RED = "\u001B[31m";
    protected static final String GREEN = "\u001B[32m";
    protected static final String BLUE = "\u001B[34m";


    protected final UserService userService = Singleton.getInstance(UserService.class);
    protected final OrderService orderService = Singleton.getInstance(OrderService.class);
    protected final ProductService productService = Singleton.getInstance(ProductService.class);

    private final Scanner scanner;

    protected static final int FIRST_CHOICE = 1;
    protected static final int SECOND_CHOICE = 2;
    protected static final int THIRD_CHOICE = 3;
    protected static final int FOURTH_CHOICE = 4;
    protected static final int FIFTH_CHOICE = 5;

    private boolean isLoggedIn = false;

    public UserInterfaceImpl(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu(String username, String password) throws ProductException {
        boolean running = true;

        while (running) {
            printMenu("Delicious Pizza", "Register", "Log in", "Exit");

            int choice = getValidatedChoice();

            switch (choice) {
                case FIRST_CHOICE -> handleRegistration(username, password);
                case SECOND_CHOICE -> handleLogin(username, password);
                case THIRD_CHOICE -> {
                    handleExit();
                    running = false;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    @Override
    public void displayMenu() {

    }

    //done, does not need
    @Override
    public void handleLogin(String username, String password) throws ProductException {
        UserRights role = userService.getUserRights(username);

        if (role == null) {
            System.out.println("Invalid username. Try again.");
        } else {
            UserInterface userInterface = role.equals(UserRights.CUSTOMER)
                ? new CustomerInterfaceImpl(scanner)
                : new EmployeeInterfaceImpl(scanner);

            isLoggedIn = userService.canUserLogIn(username, password);

            if (isLoggedIn) {
                userInterface.showMainMenuUser(username);
            }
        }
    }

    @Override
    public void handleRegistration(String username, String password) {
        printMenu("Registration", "Register as customer", "Register as employee", "Return");

        int choice = getValidatedChoice();

        switch (choice) {
            case FIRST_CHOICE -> {
                CustomerInterfaceImpl customer = new CustomerInterfaceImpl(scanner);
                customer.handleRegistration(username, password);
            }
            case SECOND_CHOICE -> {
                EmployeeInterfaceImpl empl = new EmployeeInterfaceImpl(scanner);
                empl.handleRegistration(username, password);
            }
            case THIRD_CHOICE -> displayMenu();
            default -> System.out.println("Invalid choice!");
        }
    }

    @Override
    public void handleExit() {
        System.exit(0);
    }

    @Override
    public void showMainMenuUser(String username) throws ProductException {
    }

    protected void printMenu(String title, String... options) {
        System.out.println(YELLOW + "\n------------------------" + RESET);
        System.out.println("      " + title + "    ");
        System.out.println(YELLOW + "------------------------" + RESET);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }

    protected int getValidatedChoice() {
        while (true) {
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                return choice;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

}

