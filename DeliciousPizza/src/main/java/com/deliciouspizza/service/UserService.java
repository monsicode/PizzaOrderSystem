package com.deliciouspizza.service;

import com.deliciouspizza.utils.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.Employee;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.enums.UserRights;
import com.deliciouspizza.exception.UserNotFoundException;
import com.deliciouspizza.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Set;

public class UserService {

    private final UserRepository userRepository = Singleton.getInstance(UserRepository.class);
    private static OrderService orderService;

    public boolean checkIfUserExists(String username) {
        return userRepository.isUsernamePresent(username);
    }

    public void registerCustomer(String username, String plainPassword, String address, int age) {
        if (userRepository.isUsernamePresent(username)) {
            System.out.println("User with this username already exists.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        User newUser = new Customer(username, hashedPassword, address, age);
        try {
            userRepository.addUser(newUser);
        } catch (Exception e) {
            System.out.println("Error when saving the user " + e.getMessage());
        }

        System.out.println("The user is successfully registered!");
    }

    public void registerEmployee(String username, String plainPassword) {
        if (userRepository.isUsernamePresent(username)) {
            System.out.println("User with this username already exists.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        User newUser = new Employee(username, hashedPassword);

        try {
            userRepository.addUser(newUser);
        } catch (Exception e) {
            System.out.println("Error when saving the user " + e.getMessage());
        }

        System.out.println("The user is successfully registered!");
    }

    public boolean loginUser(String username, String plainPassword) {
        try {
            User user = userRepository.getUserByUsername(username);
            if (user.checkPassword(plainPassword)) {
                System.out.println("Log in is successful!");
                return true;

            } else {
                System.out.println("Wrong password.");
                return false;
            }

        } catch (UserNotFoundException err) {
            System.out.println(err.getMessage());
        }

        return false;
    }

    public UserRights getUserRights(String username) {
        try {
            return userRepository.getUserByUsername(username).getRights();
        } catch (UserNotFoundException err) {
            System.out.println(err.getMessage());
        }

        return null;
    }

    public void addToOrderHistory(String usernameCustomer, Order order) {
        userRepository.addToOrderHistory(usernameCustomer, order);
    }

    public Set<Order> getOrderHistory(String usernameCustomer) {
        return userRepository.getOrderHistory(usernameCustomer);
    }

}
