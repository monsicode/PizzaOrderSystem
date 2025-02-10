package com.deliciouspizza.service;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.Employee;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.enums.UserRights;
import com.deliciouspizza.exception.UserNotFoundException;
import com.deliciouspizza.repository.UserRepository;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void testRegisterCustomerSuccessfulRegistration() {
        String username = "customer";
        String password = "securePass123";
        String address = "123 Street";
        int age = 25;

        when(userRepository.isUsernamePresent(username)).thenReturn(false);

        userService.registerCustomer(username, password, address, age);
        verify(userRepository).addUser(any(Customer.class));
    }

    @Test
    void testRegisterCustomerUserAlreadyExists() {
        String username = "existingCustomer";
        String password = "password123";
        String address = "123 Street";
        int age = 25;

        when(userRepository.isUsernamePresent(username)).thenReturn(true);

        userService.registerCustomer(username, password, address, age);

        verify(userRepository, never()).addUser(any(Customer.class));
    }

    @Test
    void testRegisterEmployeeSuccessfulRegistration() {
        String username = "newEmployee";
        String password = "securePass123";

        when(userRepository.isUsernamePresent(username)).thenReturn(false);

        userService.registerEmployee(username, password);

        verify(userRepository).addUser(any(Employee.class));
    }

    @Test
    void testRegisterEmployeeUserAlreadyExists() {
        String username = "existingEmployee";
        String password = "securePass123";

        when(userRepository.isUsernamePresent(username)).thenReturn(true);

        userService.registerEmployee(username, password);

        verify(userRepository, never()).addUser(any(Employee.class));
    }


    @Test
    void testCanUserLogInSuccess() {
        String username = "testUser";
        String plainPassword = "password123";

        User mockUser = mock(User.class);
        when(userRepository.getUserByUsername(username)).thenReturn(mockUser);
        when(mockUser.checkPassword(plainPassword)).thenReturn(true);

        boolean result = userService.canUserLogIn(username, plainPassword);

        assertTrue(result);
        verify(userRepository).getUserByUsername(username);
        verify(mockUser).checkPassword(plainPassword);
    }

    @Test
    void testCanUserLogInWrongPassword() {
        String username = "testUser";
        String plainPassword = "wrongPassword";

        User mockUser = mock(User.class);
        when(userRepository.getUserByUsername(username)).thenReturn(mockUser);
        when(mockUser.checkPassword(plainPassword)).thenReturn(false);

        boolean result = userService.canUserLogIn(username, plainPassword);

        assertFalse(result);
        verify(mockUser).checkPassword(plainPassword);
    }

    @Test
    void testCanUserLogInUserNotFound() {
        String username = "nonExistingUser";
        String plainPassword = "password";

        when(userRepository.getUserByUsername(username))
            .thenThrow(new UserNotFoundException("User not found: " + username));

        boolean result = userService.canUserLogIn(username, plainPassword);

        assertFalse(result);
        verify(userRepository).getUserByUsername(username);
    }

    @Test
    void testGetUserRights() {
        String usernameCustomer = "penka";
        User user = new Customer("penka", "123", "adress", 23);
        when(userRepository.getUserByUsername(usernameCustomer)).thenReturn(user);

        assertEquals(UserRights.CUSTOMER, userService.getUserRights(usernameCustomer));
    }

    @Test
    void testGetUserRightsWithNotFoundUser() {
        when(userRepository.getUserByUsername("usernameCustomer")).thenThrow(
            new UserNotFoundException("User with username penka not found"));

        try (LogCaptor logCaptor = LogCaptor.forClass(UserService.class)) {
            userService.getUserRights("usernameCustomer");
            assertTrue(logCaptor.getErrorLogs().contains("User with username penka not found"));
        }
    }


    @Test
    void testGetOrderHistory() {
        Set<Order> history = new HashSet<>();

        when(userRepository.getOrderHistory("user")).thenReturn(history);
        assertEquals(history, userService.getOrderHistory("user"));
    }


}
