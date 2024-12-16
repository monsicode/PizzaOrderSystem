package com.deliciouspizza.entity.user;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mindrot.jbcrypt.BCrypt.gensalt;

public class UserTest {

    private User customer;

    @Test
    void testCheckPasswordNullPlainPw() {
        customer = new Customer("a",
            BCrypt.hashpw("b", gensalt()),
            "c", 1);

        assertFalse(() -> customer.checkPassword(null),
            "Should return false if null value is entered");

    }

    @Test
    void testEqualsSame() {
        customer = new Customer("a", "b", "c", 1);
        User user = new Customer("a", "123", "d", 2);

        assertEquals(customer, user, "Users with identical usernames should be equal");
    }

    @Test
    void testEqualsDifferent() {
        customer = new Customer("a", "b", "c", 1);
        User user = new Customer("b", "123", "d", 2);

        assertNotEquals(customer, user, "Users with different usernames should not be equal");
    }

    @Test
    void testEqualsCustomerEmployee() {
        customer = new Customer("a", "b", "c", 1);
        User user = new Employee();

        assertNotEquals(customer, user, "Users with same usernames nut different roles should not be equal");
    }

    @Test
    void testHashCodeSame() {
        customer = new Customer("a", "b", "c", 1);
        User user = new Customer("a", "123", "d", 2);

        assertEquals(customer.hashCode(), user.hashCode(), "Users with identical usernames should have same hash");
    }

    @Test
    void testHashCodeDifferent() {
        customer = new Customer("a", "b", "c", 1);
        User user = new Customer("b", "123", "d", 2);

        assertNotEquals(customer.hashCode(), user.hashCode(),
            "Users with different usernames should not have same hash");
    }

    @Test
    void testHashCodeCustomerEmployee() {
        customer = new Customer("a", "b", "c", 1);
        User user = new Employee("a", "123");

        assertNotEquals(customer, user, "Users with same usernames nut different roles should not have same hash");
    }
}
