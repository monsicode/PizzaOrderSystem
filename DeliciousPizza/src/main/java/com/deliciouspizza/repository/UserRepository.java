package com.deliciouspizza.repository;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.exception.UserNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {

    private Map<String, User> users = new ConcurrentHashMap<>();
    private static final String USER_FILE = "data-storage/users.json";
    private final File jsonFile = new File(USER_FILE);
    TypeReference<Map<String, User>> typeRef = new TypeReference<>() {
    };

    private static final Logger LOGGER = LogManager.getLogger(UserRepository.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.findAndRegisterModules();
    }

    public UserRepository() {
        users = loadUsers();
    }

    private Map<String, User> loadUsers() {
        try {
            if (jsonFile.exists() && jsonFile.length() > 0) {
                users = OBJECT_MAPPER.readValue(jsonFile, typeRef);
                return users;
            } else {
                return new ConcurrentHashMap<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new ConcurrentHashMap<>();
        }
    }

    private void saveUsers() {
        try {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(jsonFile, users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public void addUser(User user) {
        LOGGER.info("Adding new user with username: {}", user.getUsername());
        users.put(user.getUsername(), user);
        saveUsers();
    }

    public User getUserByUsername(String username) {
        User user = users.get(username);

        if (user == null) {
            throw new UserNotFoundException("User with username " + username + " not found");
        }

       // LOGGER.info("User {} successfully retrieved.", username);
        return user;
    }

    public boolean isUsernamePresent(String username) {
        return users.containsKey(username);
    }

    public Map<String, User> getAllUsers() {
        LOGGER.info("Retrieving all users. Total count: {}", users.size());
        return Collections.unmodifiableMap(users);
    }

    public void addToOrderHistory(String usernameCustomer, Order order) {
        try {
            User user = getUserByUsername(usernameCustomer);
            if (!(user instanceof Customer customer)) {
                throw new IllegalStateException("User is not of type Customer!");
            }

            customer.addOrderToHistory(order);
            saveUsers();

            LOGGER.info("Order successfully added to history for user: {}", usernameCustomer);

        } catch (UserNotFoundException err) {
            LOGGER.error(err.getMessage());
        }
    }

    //check if its a bad/good practice ; can make it only for customers directly
    public Set<Order> getOrderHistory(String usernameCustomer) {
        try {
            User user = getUserByUsername(usernameCustomer);

            if (!(user instanceof Customer customer)) {
                throw new IllegalStateException("User is not of type Customer!");
            }

            return customer.getOrderHistory();

        } catch (UserNotFoundException err) {
            LOGGER.error(err.getMessage());
        }
        return null;
    }

    public int getAgeCustomer(String username) {
        try {
            User user = getUserByUsername(username);

            if (!(user instanceof Customer customer)) {
                throw new IllegalStateException("User is not of type Customer!");
            }

            return customer.getAge();

        } catch (UserNotFoundException err) {
            LOGGER.error(err.getMessage());
        }

        return 0;

    }

}
