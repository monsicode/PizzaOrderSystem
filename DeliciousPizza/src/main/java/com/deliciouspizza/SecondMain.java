//package com.deliciouspizza;
//
//import com.deliciouspizza.entity.order.Order;
//import com.deliciouspizza.entity.product.Drink;
//import com.deliciouspizza.entity.product.Pizza;
//import com.deliciouspizza.entity.product.Product;
//import com.deliciouspizza.entity.user.Customer;
//import com.deliciouspizza.entity.user.User;
//import com.deliciouspizza.repository.UserRepository;
//import com.deliciouspizza.service.UserService;
//import com.deliciouspizza.utils.DrinkType;
//import com.deliciouspizza.utils.DrinkVolume;
//import com.deliciouspizza.utils.PizzaSize;
//import com.deliciouspizza.utils.PizzaType;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
//
//public class SecondMain {
//
//    public static void main(String[] args) {
//
//        UserService userService = new UserService();
////        userService.registerCustomer("daka", "password123", "ул. Пиротска 10", 25);
////        userService.registerCustomer("monka", "password123", "ул. Пиротска 10", 25);
//
////        Customer user1 = new Customer("daka", "password123", "ул. Пиротска 10", 25 );
//
//        Product pizza = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
//        Product drink = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);
//
//        Map<String, Integer> productsWithQuantities1 = new HashMap<>();
//        productsWithQuantities1.put(pizza.generateKey(), 2);
//        productsWithQuantities1.put(drink.generateKey(), 1);
//
//        Order order1 = new Order(productsWithQuantities1, "123 Main St.");
//
//        userService.addToOrderHistory("monka", order1 );
////
////        user1.addOrderToHistory(order1);
////
////        System.out.println(user1.getOrderHistory());
//
//       // Scanner scanner = new Scanner(System.in);
//
////        //Registration
////        System.out.println("Регистрация на нов потребител");
////
////        System.out.print("Въведете потребителско име: ");
////        String username = scanner.nextLine();
////
////        System.out.print("Въведете парола: ");
////        String password = scanner.nextLine();
////
////        System.out.print("Въведете адрес: ");
////        String address = scanner.nextLine();
////
////        System.out.print("Въведете възраст: ");
////        int age = Integer.parseInt(scanner.nextLine());
////
////        userService.registerCustomer(username, password, address, age);
//
////         Логване
////        System.out.println("\nЛогване на потребител");
////        System.out.println("----------------------\n");
////
////        System.out.print("Въведете потребителско име: ");
////        String loginUsername = scanner.nextLine();
////
////        System.out.print("Въведете парола: ");
////        String loginPassword = scanner.nextLine();
////
////       boolean isLoggedIn = userService.loginUser(loginUsername, loginPassword);
////
////        // Логване с грешна парола
////        if (!isLoggedIn) {
////            System.out.print("\nОпитайте отново с правилна парола или въведете нова: ");
////            String retryPassword = scanner.nextLine();
////            isLoggedIn = userService.loginUser(loginUsername, retryPassword);
////        }
////
////        // Завършване на програмата
////        if (isLoggedIn) {
////            System.out.println("\nУспешно сте влезли в системата.");
////        } else {
////            System.out.println("\nНе успяхте да влезете в системата.");
////        }
////
////        // Затваряме скенера
////        scanner.close();
////    }
//    }
//}