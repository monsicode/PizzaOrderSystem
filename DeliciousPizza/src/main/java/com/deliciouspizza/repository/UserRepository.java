package com.deliciouspizza.repository;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.exception.UserNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserRepository {

    private Map<String, User> users = new HashMap<>();
    private static final String USER_FILE = "src/main/resources/users.json";
    private final ObjectMapper objectMapper;
    private final File jsonFile = new File(USER_FILE);
    TypeReference<Map<String, User>> typeRef = new TypeReference<>() {
    };

    public UserRepository() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
//        objectMapper.registerSubtypes(Customer.class, Employee.class);
        users = loadUsers();
    }

    private Map<String, User> loadUsers() {
        try {
            if (jsonFile.exists() && jsonFile.length() > 0) {
                users = objectMapper.readValue(jsonFile, typeRef);
                return users;
            } else {
                return new HashMap<>();
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void saveUsers() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public void addUser(User user) {
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
        return users;
    }

    public void addToOrderHistory(String usernameCustomer, Order order) {
        try {
            User user = getUserByUsername(usernameCustomer);
            if (!(user instanceof Customer customer)) {
                throw new IllegalStateException("User is not of type Customer!");
            }

            customer.addOrderToHistory(order);

            saveUsers();

        } catch (UserNotFoundException err) {
            System.out.println(err.getMessage());
        }
    }

    public Set<Order> getOrderHistory(String usernameCustomer) {
        try {
            User user = getUserByUsername(usernameCustomer);

            if (!(user instanceof Customer customer)) {
                throw new IllegalStateException("User is not of type Customer!");
            }

            return customer.getOrderHistory();

        } catch (UserNotFoundException err) {
            System.out.println(err.getMessage());
        }
        return null;
    }

}
