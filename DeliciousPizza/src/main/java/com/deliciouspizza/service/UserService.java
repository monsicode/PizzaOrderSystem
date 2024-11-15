package com.deliciouspizza.service;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private static final UserRepository userRepository = Singleton.getInstance(UserRepository.class);

    public boolean checkIfUserExists(String username) {
        return userRepository.userExists(username);
    }

    public void registerCustomer(String username, String plainPassword, String address, int age) {
        if (userRepository.userExists(username)) {
            System.out.println("Потребителят с това потребителско име вече съществува.");
            return;
        }

        // Хеширане на паролата
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        // Създаване на нов потребител
        User newUser = new Customer(username, hashedPassword, address, age);
        try {
            userRepository.addUser(newUser);
        } catch (Exception e) {
            System.out.println("Грешка при запис на потребителя: " + e.getMessage());
        }

        System.out.println("Потребителят е успешно регистриран.");
    }

    public boolean loginUser(String username, String plainPassword) {
        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            System.out.println("Потребителят не съществува.");
            return false;
        }

        if (user.checkPassword(plainPassword)) {
            System.out.println("Логването е успешно.");
            return true;

        } else {
            System.out.println("Грешна парола.");
            return false;
        }
    }

    public void addToOrderHistory(String usernameCustomer, Order order) {
        userRepository.addToOrderHistory(usernameCustomer, order);
    }
}
