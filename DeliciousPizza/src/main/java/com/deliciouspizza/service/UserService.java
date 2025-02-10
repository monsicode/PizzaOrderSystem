package com.deliciouspizza.service;

import com.deliciouspizza.utils.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.Employee;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.enums.UserRights;
import com.deliciouspizza.exception.UserNotFoundException;
import com.deliciouspizza.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Set;

public class UserService {

    private final UserRepository userRepository;
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    public UserService() {
        userRepository = Singleton.getInstance(UserRepository.class);
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerCustomer(String username, String plainPassword, String address, int age) {
        if (userRepository.isUsernamePresent(username)) {
            LOGGER.warn("User with username {} already exists.", username);
            return;
        }

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        User newUser = new Customer(username, hashedPassword, address, age);
        userRepository.addUser(newUser);

        LOGGER.info("Customer {} is successfully registered!", username);
    }

    public void registerEmployee(String username, String plainPassword) {
        if (userRepository.isUsernamePresent(username)) {
            LOGGER.warn("Employee with username {} already exists.", username);
            return;
        }

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        User newUser = new Employee(username, hashedPassword);
        userRepository.addUser(newUser);

        LOGGER.info("Employee {} is successfully registered!", username);
    }

    public boolean canUserLogIn(String username, String plainPassword) {
        try {
            User user = userRepository.getUserByUsername(username);
            if (user.checkPassword(plainPassword)) {
                LOGGER.info("Log in for user {} is successful!", username);
                return true;

            } else {
                LOGGER.warn("Wrong password.");
                return false;
            }

        } catch (UserNotFoundException err) {
            LOGGER.error(err.getMessage(), err);
        }

        return false;
    }

    public UserRights getUserRights(String username) {
        try {
            return userRepository.getUserByUsername(username).getRights();
        } catch (UserNotFoundException err) {
            LOGGER.error(err.getMessage());
        }

        return null;
    }

//    public void addToOrderHistory(String usernameCustomer, Order order) {
//        userRepository.addToOrderHistory(usernameCustomer, order);
//    }

    public Set<Order> getOrderHistory(String usernameCustomer) {
        return userRepository.getOrderHistory(usernameCustomer);
    }

}
