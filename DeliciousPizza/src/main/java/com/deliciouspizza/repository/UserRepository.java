package com.deliciouspizza.repository;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserRepository {
    //Petkan:passwd, age, addres ....

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
                Map<String, User> users = objectMapper.readValue(jsonFile, typeRef);
                return users;
            } else {
                return new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Записване на потребители в JSON файл
    private void saveUsers() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
        saveUsers();
    }

    // Получаване на потребител по потребителско име
    public User findUserByUsername(String username) {
        User user = users.get(username);
        if (user == null) {
            System.out.println("Не беше намерен потребител с име: " + username);
        }
        return user;
    }

    // Проверка дали потребител съществува
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    // Връщане на всички потребители (по избор)
    public Map<String, User> getAllUsers() {
        return users;
    }

    public void addToOrderHistory(String usernameCustomer, Order order) {
        User user = findUserByUsername(usernameCustomer);

        if (user == null) {
            System.out.println("Потребителят не съществува.");
            return;
        }
        //check type if customer

        Customer customer = (Customer) user;

        customer.addOrderToHistory(order);

        saveUsers();
    }

    public Set<Order> getOrderHistory(String usernameCustomer) {
        User user = findUserByUsername(usernameCustomer);

        if (user == null) {
            System.out.println("Потребителят не съществува.");
        }

        //check type if customer
        Customer customer = (Customer) user;

        return customer.getOrderHistory();
    }

}
