package com.deliciouspizza.mains;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.service.UserService;

import java.time.LocalDateTime;
import java.util.Scanner;

public class TestingUserService {

    public static void main(String[] args) {
        UserService userService = new UserService();
        OrderService orderService = new OrderService();

        Scanner scanner = new Scanner(System.in);

//        System.out.println("Регистриране на нов клиент...");
//        System.out.print("Enter username: ");
//        String username = scanner.nextLine();
//        System.out.print("Enter password: ");
//        String password = scanner.nextLine();
//        System.out.print("Enter address: ");
//        String address = scanner.nextLine();
//        System.out.print("Enter age: ");
//        int age = scanner.nextInt();
//        scanner.nextLine(); // За да хванем новия ред
//        userService.registerCustomer(username, password, address, age);
//
//        System.out.println("\nПроверка дали потребителят съществува...");
//        System.out.println("Enter username to check: ");
//        String userToCheck = scanner.nextLine();
//        boolean userExists = userService.checkIfUserExists(userToCheck);
//        System.out.println("User exists: " + userExists);
//
//        System.out.println("Enter username to start new order: ");
//        String userHistory = scanner.nextLine();
//
//        orderService.startNewOrder(userHistory);
//        orderService.addProductToActiveOrder(userHistory, "sauce_garlic_sauce", 4);
//        orderService.addProductToActiveOrder(userHistory, "pizza_pepperoni", 4);
        //orderService.finalizeOrder("monkata");
//
        //orderService.processCurrentOrder();

//        LocalDateTime start = LocalDateTime.of(2024, 11, 17, 0, 0);
//        LocalDateTime end = LocalDateTime.of(2024, 11, 17, 23, 59);
//
//        System.out.println(orderService.getProfitInPeriod(start, end));

//        userService.getOrderHistory(userHistory);

        // Логин на потребител
//        System.out.println("\nОпит за логин...");
//        boolean loginSuccess = userService.loginUser("customer1", "password123");
//        System.out.println("Login successful: " + loginSuccess);

        // Добавяне на поръчка към историята на клиента
//        if (loginSuccess) {
//            Order order = new Order();  // Това трябва да е конкретна поръчка, която създавате
//            order.setOrderDetails("Pizza, Cola");
//            userService.addToOrderHistory("customer1", order);
//            System.out.println("Order added to customer history.");
//
//        }
    }
}