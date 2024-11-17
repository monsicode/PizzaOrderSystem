package com.deliciouspizza.ui;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class CustomerInterfaceImpl implements CustomerInterface {

    private final UserService userService = new UserService();
    private final OrderService orderService = new OrderService();
    private static final ProductService PRODUCT_SERVICE = Singleton.getInstance(ProductService.class);
    private Scanner scanner;

    public CustomerInterfaceImpl(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void createOrder() {

    }

    @Override
    public void viewHistoryOfOrders() {

    }

    @Override
    public void viewProductMenu() {

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
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine();

        userService.registerCustomer(username, password, address, age);
    }

    @Override
    public void handleExit() {
        System.exit(0);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public void showOrderMenu(String username) {
        System.out.println(" Welcome to Delicious Pizza " + username + " !");
        System.out.println("------------------------");
        System.out.println("1. Create new order");
        System.out.println("2. View order history");
        System.out.println("3. Menu ");
        System.out.println("4. Exit");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                createOrder(username);
                break;
            case 2:
                System.out.println("История на поръчките:");
                Set<Order> orderHistory = userService.getOrderHistory(username);
                printOrderHistory(orderHistory);
                break;
            case 3:
                printMenu();
                break;
            case 4:
                System.out.println("Exiting...");
                return; // Излизане от поръчковото меню и връщане към основното меню
            default:
                System.out.println("Невалиден избор!");
        }
    }

    @SuppressWarnings("checkstyle:MethodLength")
    public void createOrder(String username) {
        orderService.startNewOrder(username);

        boolean addingProducts = true;

        while (addingProducts) {
            System.out.println(" What would you like to order?");
            String productKey = scanner.nextLine();

            System.out.println(" How many? ");
            int quantity = scanner.nextInt();
            scanner.nextLine();

            orderService.addProductToActiveOrder(username, productKey, quantity);

            System.out.println(" Would you like to order something else? (Y/N)");
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("N")) {
                orderService.finalizeOrder(username);
                addingProducts = false;
                System.out.println("Вашата поръчка беше успешно финализирана.");
            } else if (!response.equalsIgnoreCase("Y")) {
                System.out.println("Моля, отговорете с 'Y' за да добавите още продукти или 'N' за да завършите.");
            }
        }

    }

    private void printOrderHistory(Set<Order> orderHistory) {
        int count = 1;
        for (Order order : orderHistory) {
            System.out.println("Поръчка #" + count + ":");

            LocalDateTime orderDate = order.getOrderDate();  // Предполага се, че това е LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = orderDate.format(formatter);

            System.out.println("Дата на поръчката: " + formattedDate);

            for (Map.Entry<String, Integer> entry : order.getOrder().entrySet()) {
                String product = entry.getKey().replaceAll("_", " ");
                System.out.printf(" - %s, Количество: %d\n", product, entry.getValue());
            }

            count++;
            System.out.println("-----------------------------------");
        }
    }

    private static void printMenu() {
        System.out.println("Списък на активните продукти:");
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
