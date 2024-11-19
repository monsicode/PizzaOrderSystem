package com.deliciouspizza.service;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.Employee;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.exception.UserNotFoundException;
import com.deliciouspizza.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Set;

public class UserService {

    private static final UserRepository USER_REPOSITORY = Singleton.getInstance(UserRepository.class);
    private static OrderService orderService;

    public boolean checkIfUserExists(String username) {
        return USER_REPOSITORY.isUsernamePresent(username);
    }

    public void registerCustomer(String username, String plainPassword, String address, int age) {
        if (USER_REPOSITORY.isUsernamePresent(username)) {
            System.out.println("User with this username already exists.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        User newUser = new Customer(username, hashedPassword, address, age);
        try {
            USER_REPOSITORY.addUser(newUser);
        } catch (Exception e) {
            System.out.println("Error when saving the user " + e.getMessage());
        }

        System.out.println("The user is successfully registered!");
    }

    public void registerEmployee(String username, String plainPassword) {
        if (USER_REPOSITORY.isUsernamePresent(username)) {
            System.out.println("User with this username already exists.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        User newUser = new Employee(username, hashedPassword);

        try {
            USER_REPOSITORY.addUser(newUser);
        } catch (Exception e) {
            System.out.println("Error when saving the user " + e.getMessage());
        }

        System.out.println("The user is successfully registered!");
    }

    public boolean loginUser(String username, String plainPassword) {
        try {
            User user = USER_REPOSITORY.getUserByUsername(username);
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

    public void addToOrderHistory(String usernameCustomer, Order order) {
        USER_REPOSITORY.addToOrderHistory(usernameCustomer, order);
    }

    public Set<Order> getOrderHistory(String usernameCustomer) {
        return USER_REPOSITORY.getOrderHistory(usernameCustomer);
    }

}
