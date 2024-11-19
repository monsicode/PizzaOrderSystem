package com.deliciouspizza.ui;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.product.Sauce;
import com.deliciouspizza.enums.DrinkType;
import com.deliciouspizza.enums.DrinkVolume;
import com.deliciouspizza.enums.PizzaSize;
import com.deliciouspizza.enums.PizzaType;
import com.deliciouspizza.enums.SauceType;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.service.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class EmployeeInterfaceImpl implements EmployeeInterface {

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";

    private static final String ADMIN_HASHED_PASSWORD = "$2a$10$QVQziCS3KXdEwgTQrT3LieiUrr5yd1iuwwYgOymQFoPqbnTJL1csq";
    private static final int ATTEMPTS_PASSWORD = 3;
    //to think about / fix
    private static final UserService USER_SERVICE = Singleton.getInstance(UserService.class);
    private static final OrderService ORDER_SERVICE = Singleton.getInstance(OrderService.class);
    private static final ProductService PRODUCT_SERVICE = Singleton.getInstance(ProductService.class);
    private final Scanner scanner;

    private boolean isLoggedIn = false;

    private static final int FIRST_CHOICE = 1;
    private static final int SECOND_CHOICE = 2;
    private static final int THIRD_CHOICE = 3;
    private static final int FOURTH_CHOICE = 4;
    private static final int FIFTH_CHOICE = 5;
    private static final int SIXTH_CHOICE = 6;
    private static final int SEVENTH_CHOICE = 7;
    private static final int EIGHTH_CHOICE = 8;

    public EmployeeInterfaceImpl(Scanner scanner) {
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

        isLoggedIn = USER_SERVICE.loginUser(username, password);

        if (isLoggedIn) {
            showMainMenuEmployee(username);
        }
    }

    private static boolean verifyPassword(String inputPassword) {
        return BCrypt.checkpw(inputPassword, ADMIN_HASHED_PASSWORD);
    }

    private static boolean verifyAdminPassword(Scanner scanner) {
        int attempts = ATTEMPTS_PASSWORD;
        while (attempts > 0) {
            System.out.print("Enter admin password: ");
            String inputPassword = scanner.nextLine();

            if (verifyPassword(inputPassword)) {
                System.out.println("Access granted.");
                return true;
            } else {
                attempts--;
                System.out.println("Invalid password. Attempts left: " + attempts + "\n");
            }
        }
        return false;
    }

    @Override
    public void handleRegistration() {

        if (!verifyAdminPassword(scanner)) {
            System.out.println("Access denied. Cannot register a new employee.");
            return;
        }

        System.out.println("\n");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.println("\n");

        USER_SERVICE.registerEmployee(username, password);
    }

    @Override
    public void handleExit() {
        System.exit(0);
    }

    @Override
    public void addNewProduct() {
        printMenu("What type of product would you like to add?", "Pizza", "Drink", "Sauce");

        int choice = getValidatedChoice();

        Product product = switch (choice) {
            case FIRST_CHOICE -> createPizza();
            case SECOND_CHOICE -> createDrink();
            case THIRD_CHOICE -> createSauce();
            default -> {
                System.out.println("Invalid choice! No product created.");
                yield null;
            }
        };

        PRODUCT_SERVICE.addNewProduct(product);
    }

    @Override
    public void processOrder() {
        ORDER_SERVICE.processCurrentOrder();
    }

    @Override
    public void viewPendingOrders() {
        System.out.println(ORDER_SERVICE.getPendingOrders());
    }

    public void showMainMenuEmployee(String username) {
        boolean continueSession = true;

        while (continueSession) {
            printMainMenuCustomer(username);

            int choice = getValidatedChoice();
            switch (choice) {
                case FIRST_CHOICE -> addNewProduct();
                case SECOND_CHOICE -> processOrder();
                case THIRD_CHOICE -> viewPendingOrders();
                case FOURTH_CHOICE -> System.out.println(PRODUCT_SERVICE.getAllInactiveProducts());
                case FIFTH_CHOICE -> viewProductMenu();
                case SIXTH_CHOICE -> maintainProduct(username);
                case SEVENTH_CHOICE -> reports(username);
                case EIGHTH_CHOICE -> {
                    System.out.println("Logging out...");
                    continueSession = false;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
        displayMenu();
    }

    private void reports(String username) {

        boolean seeingReports = true;

        while (seeingReports) {
            printMenu("Reports", "See number of orders in period", "See profit in period", "Return");

            int choice = getValidatedChoice();
            switch (choice) {
                //  case FIRST_CHOICE -> deactivateProduct();
                //case SECOND_CHOICE -> activateProduct();
                case THIRD_CHOICE -> seeingReports = false;
                default -> System.out.println("Invalid choice!");
            }
        }

        showMainMenuEmployee(username);
    }

    private void getNumberOfOrders() {
        System.out.println("\nYou need to enter time period");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("\nYou need to enter time period");

        System.out.println("From (yyyy-MM-dd):");
        String fromInput = scanner.nextLine();
        LocalDate from = LocalDate.parse(fromInput, formatter);

        System.out.println("To (yyyy-MM-dd):");
        String toInput = scanner.nextLine();
        LocalDate to = LocalDate.parse(toInput, formatter);

        int numberOfrders = ORDER_SERVICE.getCountOrderInPeriod(from, to);

    }

    private void maintainProduct(String username) {
        boolean maintaining = true;

        while (maintaining) {
            printMenu("Maintain product", "Deactivate product", "Activate product", "Add more in stock", "Return");

            int choice = getValidatedChoice();
            switch (choice) {
                case FIRST_CHOICE -> deactivateProduct();
                case SECOND_CHOICE -> activateProduct();
                case THIRD_CHOICE -> System.out.println("In the process of making");
                case FOURTH_CHOICE -> maintaining = false;
                default -> System.out.println("Invalid choice!");
            }
        }

        showMainMenuEmployee(username);
    }

    private void deactivateProduct() {
        System.out.println("\nEnter product key to deactivate product:");
        String productKey = scanner.nextLine();

        Product product = null;

        try {
            product = PRODUCT_SERVICE.getProductByKey(productKey);
        } catch (ProductDoesNotExistException err) {
            System.out.println(err.getMessage());
        }

        if (product != null) {
            PRODUCT_SERVICE.deactivateProduct(product);
        }

    }

    private void activateProduct() {
        System.out.println("\nEnter product key to activate product:");
        String productKey = scanner.nextLine();

        Product product = null;

        try {
            product = PRODUCT_SERVICE.getProductByKey(productKey);
        } catch (ProductDoesNotExistException err) {
            System.out.println(err.getMessage());
        }

        if (product != null) {
            PRODUCT_SERVICE.activateProduct(product);
        }

    }

    private void printMainMenuCustomer(String username) {
        System.out.println("\n Welcome to Delicious Pizza " + BLUE + username + RESET + " !");
        System.out.println("------------------------");
        System.out.println("1. Add new product");
        System.out.println("2. Process next order");
        System.out.println("3. View pending orders");
        System.out.println("4. View deactivated products");
        System.out.println("5. Current menu");
        System.out.println("6. Maintain product");
        System.out.println("7. Reports");
        System.out.println("8. Log out \n");
    }

    private void printMenu(String title, String... options) {
        System.out.println("\n" + title);
        System.out.println(BLUE + "------------------------" + RESET);
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

    private <T extends Enum<T>> void displayProductTypes(Class<T> enumClass) {
        int i = 1;
        for (T type : enumClass.getEnumConstants()) {
            System.out.println(i++ + ". " + type.name());
        }
    }

    private <T extends Enum<T>> T getProductChoice(Class<T> enumClass, int choice) {
        try {
            return enumClass.getEnumConstants()[choice - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid choice! No product created.");
            return null;
        }
    }

    private Product createPizza() {
        System.out.println("\nSelect the type of pizza:");
        System.out.println(BLUE + "------------------------" + RESET);
        displayProductTypes(PizzaType.class);

        int pizzaChoice = getValidatedChoice();
        PizzaType selectedPizzaType = getProductChoice(PizzaType.class, pizzaChoice);

        System.out.println("\nWhat size would you like to include:");
        System.out.println(BLUE + "------------------------" + RESET);
        displayProductTypes(PizzaSize.class);

        int pizzaSize = getValidatedChoice();
        PizzaSize selectedPizzaSize = getProductChoice(PizzaSize.class, pizzaSize);

        Pizza pizza = new Pizza(selectedPizzaType, selectedPizzaSize);
        return pizza;
    }

    private Product createDrink() {
        System.out.println("\nSelect the type of drink:");
        System.out.println(BLUE + "------------------------" + RESET);
        displayProductTypes(DrinkType.class);

        int drinkChoice = getValidatedChoice();
        DrinkType selectDrink = getProductChoice(DrinkType.class, drinkChoice);

        System.out.println("\nWhat size would you like to include:");
        System.out.println(BLUE + "------------------------" + RESET);
        displayProductTypes(PizzaSize.class);

        int drinkVolume = getValidatedChoice();
        DrinkVolume selectDrinkVolume = getProductChoice(DrinkVolume.class, drinkVolume);

        Drink drink = new Drink(selectDrink, selectDrinkVolume);
        return drink;
    }

    private Product createSauce() {
        System.out.println("\nSelect the type of sauce:");
        System.out.println(BLUE + "------------------------" + RESET);
        displayProductTypes(SauceType.class);

        int sauceChoice = getValidatedChoice();
        SauceType selectSauce = getProductChoice(SauceType.class, sauceChoice);

        Sauce sauce = new Sauce(selectSauce);
        return sauce;
    }

    private void viewProductMenu() {
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

}
