package com.deliciouspizza.repository;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.Employee;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.exception.UserNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)

public class UserRepositoryTest {

    @Mock
    private ObjectMapper objectMapper;

    private UserRepository userRepository;

    @TempDir
    Path tempDir;

    private Map<String, User> users;
    private File tempFileUsers;

    @BeforeEach
    void setUp() throws IOException {
        users = new ConcurrentHashMap<>();

        tempFileUsers = tempDir.resolve("tempUsers.json").toFile();
        String jsonContent = """
            {"worker1" : {
                 "username" : "worker1",
                 "rights" : "ADMIN",
                 "hashedPassword" : "$2a$10$rIGbmBFYyzqbVq3Sw03g7.cV26WMV7p7YFSavanwKdt980UBM0vs2",
                 "userType" : "employee"
               },
               "monkata" : {
                 "username" : "monkata",
                 "rights" : "CUSTOMER",
                 "address" : "monkata str.",
                 "age" : 22,
                 "orderHistory" : [ {
                   "order" : {
                     "pizza_pepperoni_small" : 10
                   },
                   "statusOrder" : "COMPLETED",
                   "orderDate" : [ 2024, 11, 21, 20, 27, 16, 436522400 ],
                   "totalPrice" : 90.0,
                   "usernameCustomer" : "monkata",
                   "orderId" : 1
                 }]}}
            """;

        Files.write(tempFileUsers.toPath(), jsonContent.getBytes());

        userRepository = new UserRepository(objectMapper, tempFileUsers, users);
    }

    @Test
    void testLoadUsers() throws IOException {
        User user = new Customer();
        users.put("monkata", user);
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(users);

        userRepository.loadUsers();

        verify(objectMapper, times(1)).readValue(any(File.class), any(TypeReference.class));
        assertTrue(userRepository.getAllUsers().containsKey("monkata"));
    }

    @Test
    void testLoadUsersWithEmptyFile() throws IOException {
        userRepository = new UserRepository(objectMapper, new File(""), new ConcurrentHashMap<>());

        userRepository.loadUsers();
        assertTrue(userRepository.getAllUsers().isEmpty());
    }

    @Test
    void testLoadUsersWithIOException() throws IOException {
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenThrow(
            new IOException("Test IO Exception"));

        try (LogCaptor logCaptor = LogCaptor.forClass(UserRepository.class)) {
            userRepository.loadUsers();
            assertTrue(logCaptor.getErrorLogs().contains("Error loading users from file"));
        }
    }

    @Test
    void testAddUser() throws IOException {
        User user = new Employee("test1", "123");

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        userRepository.addUser(user);

        assertTrue(userRepository.isUsernamePresent("test1"));
    }

    @Test
    void testGetUserByUsername() {
        User user = new Customer("monkata", "1234", "monkata st.", 22);
        users.put("monkata", user);
        assertEquals(user, userRepository.getUserByUsername("monkata"));
    }

    @Test
    void testGetUserByUsernameWithNonExcistingUser() {
        assertThrows(UserNotFoundException.class, () -> userRepository.getUserByUsername("monkata"));
    }

    @Test
    void testAddToOrderHistory() {
        User user = new Customer("monkata", "1234", "monkata st.", 22);
        users.put("monkata", user);

        Order orderUser = new Order();

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);

        userRepository.addToOrderHistory("monkata", orderUser);

        assertTrue(userRepository.getOrderHistory("monkata").contains(orderUser));
    }

    @Test
    void testAddToOrderHistoryWithEmployee() {
        User user = new Employee("baba", "123");
        users.put("baba", user);

        Order orderUser = new Order();

        assertThrows(IllegalStateException.class, () -> userRepository.addToOrderHistory("baba", orderUser));
    }

    @Test
    void testAddToOrderHistoryWithNonExcistingUser() {
        Order orderUser = new Order();

        try (LogCaptor logCaptor = LogCaptor.forClass(UserRepository.class)) {
            userRepository.addToOrderHistory("baba", orderUser);
            assertTrue(logCaptor.getErrorLogs().contains("User with username baba not found"));
        }
    }

    @Test
    void testGetOrderHistory() {
        User user = new Customer("monkata", "1234", "monkata st.", 22);
        users.put("monkata", user);
        Order historyOrder = new Order();

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);

        userRepository.addToOrderHistory("monkata",historyOrder);
        userRepository.getOrderHistory("monkata");

        assertTrue(userRepository.getOrderHistory("monkata").contains(historyOrder));
    }

    @Test
    void testGetOrderHistoryWithEmployee() {
        User user = new Employee("baba", "123");
        users.put("baba", user);

        assertThrows(IllegalStateException.class, () -> userRepository.getOrderHistory("baba"));
    }

    @Test
    void testGetOrderHistoryWithNonExcistingUser() {
        try (LogCaptor logCaptor = LogCaptor.forClass(UserRepository.class)) {
            userRepository.getOrderHistory("baba");
            assertTrue(logCaptor.getErrorLogs().contains("User with username baba not found"));
        }
    }

    @Test
    void testGetAgeCustomer() {
        User user = new Customer("monkata", "1234", "monkata st.", 22);
        users.put("monkata", user);

        assertEquals(22, userRepository.getAgeCustomer("monkata"));

    }

    @Test
    void testGetAgeCustomerWithEmployee() {
        User user = new Employee();
        users.put("baba", user);

        assertThrows(IllegalStateException.class, () -> userRepository.getAgeCustomer("baba"));
    }

    @Test
    void testGetAgeCustomerWithNonExcistingUser() {
        try (LogCaptor logCaptor = LogCaptor.forClass(UserRepository.class)) {
            userRepository.getAgeCustomer("baba");
            assertTrue(logCaptor.getErrorLogs().contains("User with username baba not found"));
        }
    }

}
