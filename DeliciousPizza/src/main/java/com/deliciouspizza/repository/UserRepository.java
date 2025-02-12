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
    private String userFile = "data-storage/users.json";
    private File jsonFileUsers = new File(userFile);
    private final TypeReference<Map<String, User>> typeRef = new TypeReference<>() {
    };

    private static final Logger LOGGER = LogManager.getLogger(UserRepository.class);

    private final ObjectMapper objectMapper;

    public UserRepository(ObjectMapper objectMapper, File fileUsers, Map<String, User> users) {
        this.objectMapper = objectMapper;
        jsonFileUsers = fileUsers;
        this.users = users;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.findAndRegisterModules();
    }

    public UserRepository() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        users = loadUsers();
    }

    Map<String, User> loadUsers() {
        try {
            if (jsonFileUsers.exists() && jsonFileUsers.length() > 0) {
                users = objectMapper.readValue(jsonFileUsers, typeRef);
                return users;
            } else {
                return new ConcurrentHashMap<>();
            }
        } catch (IOException e) {
            LOGGER.error("Error loading users from file");
            return new ConcurrentHashMap<>();
        }
    }

    private void saveUsers() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFileUsers, users);
        } catch (IOException e) {
            LOGGER.error("Error saving users: {}", e.getMessage());
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

    public String getAddressCustomer(String username) {
        try {
            User user = getUserByUsername(username);

            if (!(user instanceof Customer customer)) {
                throw new IllegalStateException("User is not of type Customer!");
            }

            return customer.getAddress();

        } catch (UserNotFoundException err) {
            LOGGER.error(err.getMessage());
        }

        return null;
    }

}
