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

    public CustomerInterfaceImpl(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void viewHistoryOfOrders() {

    }

    @Override
    public void viewProductMenu() {

    }

    @Override
    public void createOrder() {

    }

    @Override
    public void displayMenu() {
        System.out.println("    Delicious Pizza     ");
        System.out.println("------------------------");
        System.out.println("1. Register");
        System.out.println("2. Log in");
        System.out.println("3. Exit");

        int choice = scanner.nextInt();
        scanner.nextLine(); // За да хванем останалия нов ред след въвеждането на числото

        switch (choice) {
            case 1:
                handleRegistration();
                break;
            case 2:
                handleLogin();
                break;
            case 3:
                handleExit();
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    @Override
    public void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        boolean isLoggedIn = userService.loginUser(username, password);

        if (isLoggedIn) {
            showOrderMenu(username);
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

    public void showOrderMenu(String username) {
        System.out.println("\n Welcome to Delicious Pizza " + YELLOW + username + RESET + " !");
        System.out.println("------------------------");
        System.out.println("1. Create new order");
        System.out.println("2. View order history");
        System.out.println("3. Menu ");
        System.out.println("4. Exit \n");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                createOrder(username);
                break;
            case 2:
                showHistoryOrdersForCustomer(username);
                showOrderMenu(username);
                break;
            case 3:
                printMenu();
                showOrderMenu(username);
                break;
            case 4:
                System.out.println("Exiting...");
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private void showHistoryOrdersForCustomer(String username) {
        Set<Order> orderHistory = userService.getOrderHistory(username);

        if (orderHistory.isEmpty()) {
            System.out.println("No orders in history.");
        } else {

            System.out.println("Order history:");
            printOrderHistory(orderHistory);
        }
    }

    public void createOrder(String username) {
        try {
            orderService.startNewOrder(username);
        } catch (IllegalStateException err) {
            System.out.println(err.getMessage());
            showCurrentOrder(username);

            finishOrEditOrder(username);
        }

        boolean creatingOrder = true;

        while (creatingOrder) {
            addingProduct(username);

            System.out.println("Would you like to order something else? (Y/N)");
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("N")) {
                showCurrentOrder(username);
                creatingOrder = finishOrEditOrder(username);

                if (creatingOrder) {
                    showOrderMenu(username);
                }

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

    public void editOrder(String username) {
        boolean editing = true;

        while (editing) {
            System.out.println("\n");
            System.out.println("------------------------");
            System.out.println("       Edit order    ");
            System.out.println("------------------------");
            System.out.println("1. Add product");
            System.out.println("2. Remove product");
            System.out.println("3. Return");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addingProduct(username);
                    showCurrentOrder(username);
                    break;
                case 2:
                    removeProduct(username);
                    showCurrentOrder(username);
                    break;
                case 3:
                    editing = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }

        finishOrEditOrder(username);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private boolean finishOrEditOrder(String username) {
        System.out.println("\n");
        System.out.println("------------------------");
        System.out.println("       Create order    ");
        System.out.println("------------------------");
        System.out.println("1. Finish order");
        System.out.println("2. Edit order");
        System.out.println("3. Return");

        int choice = scanner.nextInt();
        scanner.nextLine();

        return switch (choice) {
            case 1 -> {
                orderService.finalizeOrder(username);
                yield false;
            }
            case 2 -> {
                editOrder(username);
                yield true;
            }
            case 3 -> {
                yield false;
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

    private static void printMenu() {
        System.out.println("List with active products:");
        Map<String, Product> activeProducts = PRODUCT_SERVICE.getAllActiveProducts();
        for (Map.Entry<String, Product> entry : activeProducts.entrySet()) {
            String key = entry.getKey();
            Product product = entry.getValue();

            // Извикваме getFormattedDetails()
            String details = product.getFormattedDetails();

            // Подравняване на изхода
            System.out.printf(" - %-35s KEY: %s\n", details, key);
        }
        System.out.println("-----------------------------------");
    }

}
