package com.deliciouspizza;

import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.repository.UserRepository;
import com.deliciouspizza.service.UserService;

import java.util.Scanner;

public class SecondMain {

    public static void main(String[] args) {

        UserService userService = new UserService();
        //System.out.println("Регистрация на потребител Customer:");
        //userService.registerCustomer("daka", "password123", "ул. Пиротска 10", 25);


        // Създаваме UserService за работа с потребители
        //    UserService userService = new UserService();
          Scanner scanner = new Scanner(System.in);
//
//        // Регистрация
//        System.out.println("Регистрация на нов потребител:");
//
//        System.out.print("Въведете потребителско име: ");
//        String username = scanner.nextLine();
//
//        System.out.print("Въведете парола: ");
//        String password = scanner.nextLine();
//
//        System.out.print("Въведете адрес: ");
//        String address = scanner.nextLine();
//
//        System.out.print("Въведете възраст: ");
//        int age = Integer.parseInt(scanner.nextLine());
//
//        userService.registerCustomer(username, password, address, age);
//
        // Логване
        System.out.println("\nЛогване на потребител");
        System.out.println("----------------------\n");

        System.out.print("Въведете потребителско име: ");
        String loginUsername = scanner.nextLine();

        System.out.print("Въведете парола: ");
        String loginPassword = scanner.nextLine();

       boolean isLoggedIn = userService.loginUser(loginUsername, loginPassword);

        // Логване с грешна парола
        if (!isLoggedIn) {
            System.out.print("\nОпитайте отново с правилна парола или въведете нова: ");
            String retryPassword = scanner.nextLine();
            isLoggedIn = userService.loginUser(loginUsername, retryPassword);
        }

        // Завършване на програмата
        if (isLoggedIn) {
            System.out.println("\nУспешно сте влезли в системата.");
        } else {
            System.out.println("\nНе успяхте да влезете в системата.");
        }

        // Затваряме скенера
        scanner.close();
//    }
    }
}
