package com.deliciouspizza.service;

import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.repository.UserRepository;
import com.deliciouspizza.utils.UserRights;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

//register
//sigIn
//Exit

public class UserService {

    private UserRepository userRepository = new UserRepository();

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
}
