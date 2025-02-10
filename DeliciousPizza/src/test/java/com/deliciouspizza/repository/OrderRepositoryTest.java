package com.deliciouspizza.repository;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.enums.StatusOrder;
import com.deliciouspizza.exception.ErrorInProductNameException;
import com.deliciouspizza.exception.InactiveProductException;
import com.deliciouspizza.exception.ProductException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderRepositoryTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderRepository orderRepository;

    @TempDir
    Path tempDir;

    private File tempFilePendingOrders;
    private File tempFileHistoryOrders;

    @BeforeEach
    void setUp() {
        tempFilePendingOrders = tempDir.resolve("pendingOrders.json").toFile();
        tempFileHistoryOrders = tempDir.resolve("historyOrdersTest.json").toFile();

        orderRepository = new OrderRepository(objectMapper, tempFilePendingOrders, tempFileHistoryOrders);
    }

    @Test
    void testAddOrder() throws IOException {
        Order order = new Order();
        order.setUsernameCustomer("testUser");

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        orderRepository.addOrder(order);

        assertTrue(orderRepository.getPendingOrders().contains(order));
    }

    @Test
    void testGetNextOrder() throws InterruptedException, IOException {
        Order order = new Order();
        order.setUsernameCustomer("testUser");

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        orderRepository.addOrder(order);
        Order nextOrder = orderRepository.getNextOrder();

        assertEquals(order, nextOrder);
        assertFalse(orderRepository.getPendingOrders().contains(order));
    }

    @Test
    void testLoadPendingOrdersWithException() throws IOException {
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenThrow(
            new IOException("Test IO Exception"));

        try (LogCaptor logCaptor = LogCaptor.forClass(OrderRepository.class)) {
            orderRepository.loadPendingOrders();
            assertTrue(logCaptor.getErrorLogs().contains("Error loading pending orders!"));
        }
    }

    @Test
    void testLoadPendingOrders() throws IOException {
        Set<Order> orders = new HashSet<>();
        Order order = new Order();
        order.setUsernameCustomer("testUser");
        orders.add(order);

        when(objectMapper.readValue(tempFilePendingOrders, new TypeReference<LinkedBlockingQueue<Order>>() {
        }))
            .thenReturn(new LinkedBlockingQueue<>(orders));

        orderRepository.loadPendingOrders();
        assertTrue(orderRepository.getPendingOrders().contains(order));
    }

    @Test
    void testLoadHistoryOrders() throws IOException {
        Set<Order> orders = new HashSet<>();
        Order order = new Order();
        order.setUsernameCustomer("testUser");
        orders.add(order);

//        when(objectMapper.readValue(tempFileHistoryOrders, new TypeReference<Set<Order>>() {
//        })).thenReturn(orders);

        doReturn(orders).when(objectMapper)
            .readValue(eq(tempFileHistoryOrders), eq(new TypeReference<Set<Order>>() {
            }));

        Set<Order> result = orderRepository.loadHistoryOrders();
        assertTrue(result.contains(order));
    }

    //why this test dosent work?
    @Test
    void testLoadHistoryOrdersWithException() throws IOException {
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenThrow(
            new IOException("Test IO Exception"));

        try (LogCaptor logCaptor = LogCaptor.forClass(OrderRepository.class)) {
            orderRepository.loadHistoryOrders();
            assertTrue(logCaptor.getErrorLogs().contains("Error loading completed orders history!"));
        }
    }

    @Test
    void testCompleteOrder() throws IOException {
        Order order = new Order();
        order.setUsernameCustomer("testUser");

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        orderRepository.addOrder(order);
        orderRepository.completeOrder(order);

        assertEquals(StatusOrder.COMPLETED, order.getStatusOrder());
        assertTrue(orderRepository.getCompletedOrders().contains(order));
    }

    @Test
    void testStartNewOrder() {
        String username = "testUser";

        orderRepository.startNewOrder(username);
        assertNotNull(orderRepository.getCurrentOrderForUser(username));
    }

    @Test
    void testStartNewOrderWithAlreadyActiveOrder() {
        String username = "testUser";
        orderRepository.startNewOrder(username);

        assertThrows(IllegalStateException.class, () -> orderRepository.startNewOrder("testUser"),
            "Should throw exception if order is already started for user");
    }


    @Test
    void testAddProductToActiveOrder() throws ProductException {
        String username = "testUser";
        String productKey = "pizza_pepperoni_small";
        int quantity = 1;

        orderRepository.startNewOrder(username);
        orderRepository.addProductToActiveOrder(username, productKey, quantity);

        Order order = orderRepository.getCurrentOrderForUser(username);
        assertTrue(order.getOrder().containsKey(productKey));
        assertEquals(quantity, order.getOrder().get(productKey));
    }

    @Test
    void testAddProductToActiveOrderWithInactiveOrder() throws ProductException {
        String username = "testUser";
        String productKey = "pizza_pepperoni_small";
        int quantity = 1;

        assertThrows(IllegalStateException.class,
            () -> orderRepository.addProductToActiveOrder(username, productKey, quantity),
            "User does not have an active order");
    }

    @Test
    void testAddProductToActiveOrderWithInactiveProduct() throws InactiveProductException, ErrorInProductNameException {
        String username = "testUser";
        String productKey = "inactive_product";
        int quantity = 1;

        orderRepository.startNewOrder(username);

        assertThrows(ProductException.class,
            () -> orderRepository.addProductToActiveOrder(username, productKey, quantity));
    }


    @Test
    void testRemoveFromCurrentOrderForUser() throws ProductException {
        String username = "testUser";
        String productKey = "pizza_pepperoni_small";
        int quantity = 1;

        orderRepository.startNewOrder(username);
        orderRepository.addProductToActiveOrder(username, productKey, quantity);

        orderRepository.removeFromCurrentOrderForUser(username, productKey, quantity);

        Order order = orderRepository.getCurrentOrderForUser(username);
        assertFalse(order.getOrder().containsKey(productKey));
    }

    @Test
    void testRemoveFromCurrentOrderForUserWithInactiveOrder() throws ProductException {
        String username = "testUser";
        String productKey = "pizza_pepperoni_small";
        int quantity = 1;

        orderRepository.startNewOrder(username);

        assertThrows(IllegalStateException.class,
            () -> orderRepository.removeFromCurrentOrderForUser(username, productKey, quantity),
            "User does not have an active order");
    }

    @Test
    void testRemoveFromCurrentOrderForUserWithEmptyOrder() {
        String username = "testUser";
        String productKey = "pizza_pepperoni_small";
        int quantity = 1;

        assertThrows(IllegalStateException.class,
            () -> orderRepository.removeFromCurrentOrderForUser(username, productKey, quantity),
            "User does not have anything in his order to be removed");
    }

    @Test
    void testRemoveFromCurrentOrderForUserWithNonExistingProduct() throws ProductException {
        String username = "testUser";
        String productKey = "pizza_pepperoni_small";
        int quantity = 1;

        orderRepository.startNewOrder(username);
        orderRepository.addProductToActiveOrder(username, productKey, quantity);

        assertThrows(ProductException.class,
            () -> orderRepository.removeFromCurrentOrderForUser(username, productKey, 2));
    }

    @Test
    void testGetCurrentOrderForUserWithInactiveOrder() {
        assertThrows(IllegalStateException.class, () -> orderRepository.getCurrentOrderForUser("user"));
    }

    @Test
    void testFinalizeOrderWithInactiveOrder() {
        String username = "testUser";

        assertThrows(IllegalStateException.class, () -> orderRepository.finalizeOrder(username),
            "User does not have an active order to finalize!");
    }

    @Test
    void testFinalizeOrderWithEmptyOrder() {
        String username = "testUser";
        orderRepository.startNewOrder(username);

        assertThrows(IllegalStateException.class, () -> orderRepository.finalizeOrder(username),
            "User does has empty order, cannot finalize!");
    }

    @Test
    void testFinalizeOrder() throws IOException, ProductException {
        String username = "testUser";
        String productKey = "pizza_pepperoni_small";
        int quantity = 1;

        orderRepository.startNewOrder(username);
        orderRepository.addProductToActiveOrder(username, productKey, quantity);

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());


        try (LogCaptor logCaptor = LogCaptor.forClass(OrderRepository.class)) {
            orderRepository.finalizeOrder(username);
            assertTrue(logCaptor.getInfoLogs().contains("Order finalized for user testUser: \nOrder : \n" +
                "\tProduct: pizza_pepperoni_small; Quantity: 1"));
        }
    }


    @Test
    void testGetCountOrderInPeriod() throws IOException {
        Order order1 = new Order();
        order1.resetOrderDate();

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        orderRepository.completeOrder(order1);

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        long count = orderRepository.getCountOrderInPeriod(from, to);

        assertEquals(1, count);
    }

    @Test
    void testGetProfitInPeriod() throws IOException, InactiveProductException, ErrorInProductNameException {
        Order order1 = new Order();
        order1.addProduct("pizza_pepperoni_small", 1); //have to simulate this
        order1.resetOrderDate();

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        orderRepository.completeOrder(order1);

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        double profit = orderRepository.getProfitInPeriod(from, to);

        assertEquals(9.0, profit);
    }

    @Test
    void testIOExceptionDuringSavePendingOrders() throws IOException {
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doThrow(new IOException("Test IO Exception")).when(objectWriter).writeValue(any(File.class), any());

        try (LogCaptor logCaptor = LogCaptor.forClass(OrderRepository.class)) {
            orderRepository.savePendingOrders();
            assertTrue(logCaptor.getErrorLogs().contains("Error saving pending orders: "));
        }
    }

    @Test
    void testGetHistoryOrdersWithEmptyHistory() {
        List<Order> historyOrders = orderRepository.getHistoryOrders();
        assertTrue(historyOrders.isEmpty());
    }

    @Test
    void testGetTotalPriceOfOrderWithInactiveOrder() {
        String username = "testUser";

        try (LogCaptor logCaptor = LogCaptor.forClass(OrderRepository.class)) {
            orderRepository.getTotalPriceOfOrder(username);
            assertTrue(logCaptor.getErrorLogs().contains("User does not have an active order."));
        }

    }

    @Test
    void testGetTotalPriceOfOrder() {
        String username = "testUser";
        orderRepository.startNewOrder(username);
        double totalPrice = orderRepository.getTotalPriceOfOrder(username);

        assertEquals(0.0, totalPrice);
    }


    @Test
    void testFinalizeRepeatedOrder() throws IOException {
        Order order = new Order();
        order.setUsernameCustomer("testUser");

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        orderRepository.finalizeRepeatedOrder(order);

        assertTrue(orderRepository.getPendingOrders().contains(order));
    }

    @Test
    void testFinalizeRepeatedOrderWithNullOrder() {
        assertThrows(IllegalStateException.class, () -> orderRepository.finalizeRepeatedOrder(null),
            "Order doesn't exist to be finialized");
    }

}