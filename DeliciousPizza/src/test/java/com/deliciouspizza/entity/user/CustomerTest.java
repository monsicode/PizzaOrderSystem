package com.deliciouspizza.entity.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomerTest {

    private Customer customer;

    @Test
    void testGetOrderHistoryIsUnmodifiableCollection() {
        customer = new Customer();

        assertThrows(UnsupportedOperationException.class, () -> customer.getOrderHistory().clear(),
            "The order history is not unmodifiable collection");
    }
}
