package com.deliciouspizza.ui;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.enums.StatusOrder;
import com.deliciouspizza.exception.ErrorInProductNameException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class CustomerInterfaceImpl extends UserInterfaceImpl implements CustomerInterface {

    private final Scanner scanner;

    private boolean isLoggedIn = false;

    public CustomerInterfaceImpl(Scanner scanner) {
        super(scanner);
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
    public void showMainMenuUser(String username) {
        showMainMenuCustomer(username);
    }

    @Override
    public void viewHistoryOfOrders(String username) {
        Set<Order> orderHistory = userService.getOrderHistory(username);

        if (orderHistory.isEmpty()) {
            System.out.println("No orders in history.");
        } else {

            Map<Integer, Order> temp = new HashMap<>();
            System.out.println("Order history:");
            printOrderHistory(orderHistory, temp);
        }
    }

    @Override
    public void viewProductMenu() {
        System.out.println("List with active products:");
        Map<String, Product> activeProducts = productService.getAllActiveProducts();
        for (Map.Entry<String, Product> entry : activeProducts.entrySet()) {
            String key = entry.getKey();
            Product product = entry.getValue();

            String details = product.getFormattedDetails();

            System.out.printf(YELLOW + "- " + RESET + "%-35s KEY: %s\n", details, key);
        }
        System.out.println(YELLOW + "-----------------------------------" + RESET);
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
        System.out.println("3. Repeat an order");
        System.out.println("4. Menu ");
        System.out.println("5. Log out \n");
    }

    public void showMainMenuCustomer(String username) {
        boolean continueSession = true;

        while (continueSession) {
            printMainMenuCustomer(username);

            int choice = getValidatedChoice();

            switch (choice) {
                case FIRST_CHOICE -> createOrder(username);
                case SECOND_CHOICE -> viewHistoryOfOrders(username);
                case THIRD_CHOICE -> repeatAnOrder(username);
                case FOURTH_CHOICE -> viewProductMenu();
                case FIFTH_CHOICE -> {
                    System.out.println("Logging out...");
                    continueSession = false;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
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
        System.out.println("-----------------------------------");

        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            String productKey = entry.getKey();
            String product = entry.getKey().replaceAll("_", " ");
            product = product.substring(0, 1).toUpperCase() + product.substring(1).toLowerCase();
            int quantity = entry.getValue();
            double price = productService.getProductPriceByKey(productKey) * quantity;
            System.out.printf(YELLOW + " - " + RESET + " %-20s Quantity: %-2d  Price: $%.2f\n", product, quantity,
                price);
        }

        System.out.println("-----------------------------------");
        System.out.printf("Total Price: " + YELLOW + "$%.2f\n" + RESET,
            orderService.getTotalPriceOfOrderForCustomer(username));
    }

    private void printOrderHistory(Set<Order> orderHistory, Map<Integer, Order> orderMap) {
        int count = 1;
        for (Order order : orderHistory) {
            orderMap.put(count, order);

            System.out.println(YELLOW + "Order #" + count + ":" + RESET);

            LocalDateTime orderDate = order.getOrderDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = orderDate.format(formatter);

            System.out.println("Date of ordering: " + formattedDate);

            for (Map.Entry<String, Integer> entry : order.getOrder().entrySet()) {
                String product = entry.getKey().replaceAll("_", " ");
                System.out.printf(" - %s, Quantity: %d\n", product, entry.getValue());
            }

            count++;
            System.out.println("-----------------------------------");
        }
    }

    public void repeatAnOrder(String username) {
        Set<Order> orderHistory = userService.getOrderHistory(username);

        if (orderHistory.isEmpty()) {
            System.out.println("No order history found.");
            return;
        }

        Map<Integer, Order> orderMap = new HashMap<>();
        printOrderHistory(orderHistory, orderMap);

        System.out.print("Select an order number to repeat: ");
        int selectedOrderNumber = getValidatedChoice();

        Order selectedOrder = orderMap.get(selectedOrderNumber);

        if (selectedOrder != null) {
            selectedOrder.setStatusOrder(StatusOrder.PROCESSING);
            selectedOrder.resetOrderDate();

            orderService.finalizeRepeatedOrder(selectedOrder);
            System.out.println("Order repeated successfully!");
        } else {
            System.out.println("Invalid order number. Try again.");
        }
    }

}
