package com.deliciouspizza.service;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.exception.ProductException;
import com.deliciouspizza.exception.UnderAgedException;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.repository.UserRepository;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, userRepository, productService);
    }

    @Test
    void testStartNewOrder() {
        orderService.startNewOrder("penka");
        verify(orderRepository, times(1)).startNewOrder("penka");
    }

    @Test
    void testAddProductToActiveOrderValid() throws UnderAgedException, ProductException {
        String username = "testUser";
        String productKey = "pizza";
        int quantity = 2;

        when(userRepository.getAgeCustomer(username)).thenReturn(20);

        orderService.addProductToActiveOrder(username, productKey, quantity);

        verify(orderRepository).addProductToActiveOrder(username, productKey, quantity);
    }

    @Test
    void testAddProductToActiveOrderAgeRestriction() throws ProductException {
        String username = "testUser";
        String productKey = "beer";
        int quantity = 2;

        when(userRepository.getAgeCustomer(username)).thenReturn(17);
        when(productService.isItGoodForUnderAgedCustomers(productKey)).thenReturn(false);

        UnderAgedException thrown = assertThrows(UnderAgedException.class, () -> {
            orderService.addProductToActiveOrder(username, productKey, quantity);
        });

        assertTrue(thrown.getMessage().contains("under aged"));
        verify(orderRepository, never()).addProductToActiveOrder(username, productKey, quantity);
    }

    @Test
    void testRemoveFromCurrentOrderForUser() throws ProductException {
        String username = "testUser";
        String productKey = "pizza";
        int quantity = 1;

        orderService.removeFromCurrentOrderForUser(username, productKey, quantity);

        verify(orderRepository).removeFromCurrentOrderForUser(username, productKey, quantity);
    }

    @Test
    void testGetCurrentOrderForUser() {
        String username = "testUser";
        Map<String, Integer> mockOrder = new HashMap<>();
        mockOrder.put("pizza_pepperoni_small", 2);

        when(orderRepository.getCurrentOrderForUser(username)).thenReturn(new Order(mockOrder, username));

        Map<String, Integer> result = orderService.getCurrentOrderForUser(username);
        assertEquals(mockOrder, result);
    }

    @Test
    void testGetCurrentOrderForUserWithNotActiveOrderForUser() {
        String username = "testUser";

        when(orderRepository.getCurrentOrderForUser(username)).thenThrow( new IllegalStateException("User does not have an active order."));

        try (LogCaptor logCaptor = LogCaptor.forClass(OrderService.class)) {
            orderService.getCurrentOrderForUser(username);
            assertTrue(logCaptor.getErrorLogs().contains("User does not have an active order."));
        }

    }

    @Test
    void testFinalizeOrder() {
        String username = "testUser";

        orderService.finalizeOrder(username);
        verify(orderRepository).finalizeOrder(username);
    }

    @Test
    void testFinalizeRepeatedOrder() {
        Order mockOrder = mock(Order.class);

        doThrow(new IllegalStateException("Test Exception")).when(orderRepository).finalizeRepeatedOrder(mockOrder);
        assertDoesNotThrow(() -> orderService.finalizeRepeatedOrder(mockOrder));

        verify(orderRepository).finalizeRepeatedOrder(mockOrder);
    }

    @Test
    void testProcessCurrentOrder() throws InterruptedException {
        Order mockOrder = mock(Order.class);
        when(orderRepository.getNextOrder()).thenReturn(mockOrder);

        orderService.processCurrentOrder();
        verify(orderRepository).completeOrder(mockOrder);
    }

    @Test
    void testProcessCurrentOrderWithWarning() throws InterruptedException {
        when(orderRepository.getNextOrder()).thenReturn(null);

        try (LogCaptor logCaptor = LogCaptor.forClass(OrderService.class)) {
            orderService.processCurrentOrder();
            assertTrue(logCaptor.getWarnLogs().contains("There are no orders to process."));
        }
    }

    @Test
    void testShowCurrentOrderForUser() {
        String username = "testUser";
        Map<String, Integer> mockOrder = new HashMap<>();
        mockOrder.put("pizza_pepperoni_small", 2);

        when(orderRepository.getCurrentOrderForUser(username)).thenReturn(new Order(mockOrder, username));
        when(productService.getProductPriceByKey("pizza_pepperoni_small")).thenReturn(9.0);

        String orderSummary = orderService.showCurrentOrderForUser(username);

        assertTrue(orderSummary.contains("Your order contains:"));
        assertTrue(orderSummary.contains("Total Price"));
        assertTrue(orderSummary.contains("$18.00"));
    }

    @Test
    void testGetTotalPriceOfOrderForCustomer() {
        String username = "testUser";

        when(orderRepository.getTotalPriceOfOrder(username)).thenReturn(50.0);

        double totalPrice = orderService.getTotalPriceOfOrderForCustomer(username);
        assertEquals(50.0, totalPrice);
    }

    @Test
    void testGetPendingOrders() {
        orderService.getPendingOrders();
        verify(orderRepository).getPendingOrders();
    }

    @Test
    void testGetHistoryOfOrders() {
        orderService.getHistoryOfOrders();
        verify(orderRepository).getHistoryOrders();
    }

    @Test
    void testGetCountOrderInPeriod() {
        orderService.getCountOrderInPeriod(null, null);
        verify(orderRepository).getCountOrderInPeriod(null, null);
    }

    @Test
    void testGetProfitInPeriod() {
        orderService.getProfitInPeriod(null, null);
        verify(orderRepository).getProfitInPeriod(null, null);
    }

}
