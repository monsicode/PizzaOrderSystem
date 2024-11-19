package com.deliciouspizza.ui;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.ErrorInProductNameException;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class CustomerInterfaceImpl implements CustomerInterface {

    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";

    private final UserService userService = new UserService();
    private final OrderService orderService = Singleton.getInstance(OrderService.class);
    private static final ProductService PRODUCT_SERVICE = Singleton.getInstance(ProductService.class);
    private final Scanner scanner;

    private boolean isLoggedIn = false;

    private static final int FIRST_CHOICE = 1;
    private static final int SECOND_CHOICE = 2;
    private static final int THIRD_CHOICE = 3;
    private static final int FOURTH_CHOICE = 4;

    public CustomerInterfaceImpl(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void displayMenu() {

        boolean running = true;

        while (running) {
            printMenu("Delicious Pizza", "Register", "Log in", "Exit");

            int choice = getValidatedChoice();

            switch (choice) {
                case FIRST_CHOICE -> handleRegistration();
                case SECOND_CHOICE -> handleLogin();
                case THIRD_CHOICE -> {
                    handleExit();
                    running = false;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    @Override
    public void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        isLoggedIn = userService.loginUser(username, password);

        if (isLoggedIn) {
            showMainMenuCustomer(username);
        }
    }

    @Override
    public void handleRegistration() {
        System.out.println("\n");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.println("\n");
        userService.registerCustomer(username, password, address, age);
    }

    @Override
    public void handleExit() {
        System.exit(0);
    }

    @Override
    public void viewHistoryOfOrders(String username) {
        Set<Order> orderHistory = userService.getOrderHistory(username);

        if (orderHistory.isEmpty()) {
            System.out.println("No orders in history.");
        } else {

            System.out.println("Order history:");
            printOrderHistory(orderHistory);
        }
    }

    @Override
    public void viewProductMenu() {
        System.out.println("List with active products:");
        Map<String, Product> activeProducts = PRODUCT_SERVICE.getAllActiveProducts();
        for (Map.Entry<String, Product> entry : activeProducts.entrySet()) {
            String key = entry.getKey();
            Product product = entry.getValue();

            String details = product.getFormattedDetails();

            System.out.printf(" - %-35s KEY: %s\n", details, key);
        }
        System.out.println("-----------------------------------");
    }

    @Override
    public void createOrder(String username) {
        boolean creatingOrder = true;

        try {
            orderService.startNewOrder(username);
        } catch (IllegalStateException err) {
            System.out.println(err.getMessage());
            showCurrentOrder(username);

            creatingOrder = finishOrEditOrder(username);
        }

        while (creatingOrder) {
            addingProduct(username);

            System.out.println("Would you like to order something else? (Y/N)");
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("N")) {

                showCurrentOrder(username);
                creatingOrder = finishOrEditOrder(username);

                if (creatingOrder) {
                    showMainMenuCustomer(username);
                }
            }
        }
    }

    private void printMainMenuCustomer(String username) {
        System.out.println("\n Welcome to Delicious Pizza " + YELLOW + username + RESET + " !");
        System.out.println("------------------------");
        System.out.println("1. Create new order");
        System.out.println("2. View order history");
        System.out.println("3. Menu ");
        System.out.println("4. Log out \n");
    }

    public void showMainMenuCustomer(String username) {
        boolean continueSession = true;

        while (continueSession) {
            printMainMenuCustomer(username);

            int choice = getValidatedChoice();

            switch (choice) {
                case FIRST_CHOICE -> createOrder(username);
                case SECOND_CHOICE -> viewHistoryOfOrders(username);
                case THIRD_CHOICE -> viewProductMenu();
                case FOURTH_CHOICE -> {
                    System.out.println("Logging out...");
                    continueSession = false;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
        displayMenu();
    }

    private void addingProduct(String username) {
        System.out.println("What would you like to order?");
        String productKey = scanner.nextLine();

        System.out.println("How many? ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        try {
            orderService.addProductToActiveOrder(username, productKey, quantity);
        } catch (ErrorInProductNameException err) {

            System.out.println(err.getMessage());
            System.out.println("Do you want to try again? (Y/N)");
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("N")) {
                finishOrEditOrder(username);
            } else {
                addingProduct(username);
            }

        }
    }

    private void removeProduct(String username) {
        System.out.println("\n");
        System.out.println("What product would you like to remove? : ");
        String productKey = scanner.nextLine();

        System.out.println("How much of it? :");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        orderService.removeFromCurrentOrderForUser(username, productKey, quantity);
    }

    private void editOrder(String username) {

        boolean editing = true;

        while (editing) {

            printMenu("Edit order", "Add product", "Remove product", "Return");

            int choice = getValidatedChoice();

            switch (choice) {
                case FIRST_CHOICE -> {
                    addingProduct(username);
                    showCurrentOrder(username);
                }
                case SECOND_CHOICE -> {
                    removeProduct(username);
                    showCurrentOrder(username);
                }
                case THIRD_CHOICE -> editing = false;
                default -> System.out.println("Invalid choice!");
            }
        }
        finishOrEditOrder(username);
    }

    private boolean finishOrEditOrder(String username) {

        printMenu("Create Order", "Finish order", "Edit order");

        int choice = getValidatedChoice();

        return switch (choice) {
            case FIRST_CHOICE -> {
                orderService.finalizeOrder(username);
                yield false;
            }
            case SECOND_CHOICE -> {
                editOrder(username);
                yield true;
            }
            default -> {
                System.out.println("Invalid choice!");
                yield false;
            }
        };
    }

    private void showCurrentOrder(String username) {
        Map<String, Integer> orderMap = orderService.getCurrentOrderForUser(username);

        System.out.println("Your order contains:");

        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            String product = entry.getKey().replaceAll("_", " ");
            int quantity = entry.getValue();
            System.out.printf(" - %s, Quantity: %d\n", product, quantity);
        }
    }

    private void printOrderHistory(Set<Order> orderHistory) {
        int count = 1;
        for (Order order : orderHistory) {
            System.out.println("Order #" + count + ":");

            LocalDateTime orderDate = order.getOrderDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = orderDate.format(formatter);

            System.out.println("Date of ordering: " + formattedDate);

            for (Map.Entry<String, Integer> entry : order.getOrder().entrySet()) {
                String product = entry.getKey().replaceAll("_", " ");
                System.out.printf(" - %s, Количество: %d\n", product, entry.getValue());
            }

            count++;
            System.out.println("-----------------------------------");
        }
    }

    private void printMenu(String title, String... options) {
        System.out.println("\n------------------------");
        System.out.println("      " + title + "    ");
        System.out.println("------------------------");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }

    private int getValidatedChoice() {
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
