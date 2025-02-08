package com.deliciouspizza.repository;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.enums.StatusOrder;
import com.deliciouspizza.exception.ProductException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
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
    void setUp() throws IOException {
        // Създаване на временни файлове
        tempFilePendingOrders = tempDir.resolve("pendingOrders.json").toFile();
        tempFileHistoryOrders = tempDir.resolve("historyOrders.json").toFile();

        // Инициализация на OrderRepository с временните файлове
        orderRepository = new OrderRepository(objectMapper, tempFilePendingOrders, tempFileHistoryOrders);
    }

    @Test
    void testAddOrder() throws IOException {
        // Arrange
        Order order = new Order();
        order.setUsernameCustomer("testUser");

        // Мокване на записването във файл
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        // Act
        orderRepository.addOrder(order);

        // Assert
        assertTrue(orderRepository.getPendingOrders().contains(order));
    }

    @Test
    void testGetNextOrder() throws InterruptedException, IOException {
        // Arrange
        Order order = new Order();
        order.setUsernameCustomer("testUser");

        // Мокване на записването във файл
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        orderRepository.addOrder(order);

        // Act
        Order nextOrder = orderRepository.getNextOrder();

        // Assert
        assertEquals(order, nextOrder);
        assertFalse(orderRepository.getPendingOrders().contains(order));
    }

    @Test
    void testLoadPendingOrders() throws IOException {
        // Arrange
        Set<Order> orders = new HashSet<>();
        Order order = new Order();
        order.setUsernameCustomer("testUser");
        orders.add(order);

        when(objectMapper.readValue(tempFilePendingOrders, new TypeReference<LinkedBlockingQueue<Order>>() {}))
            .thenReturn(new LinkedBlockingQueue<>(orders));

        // Act
        orderRepository.loadPendingOrders();

        // Assert
        assertTrue(orderRepository.getPendingOrders().contains(order));
    }

    @Test
    void testLoadHistoryOrders() throws IOException {
        // Arrange
        Set<Order> orders = new HashSet<>();
        Order order = new Order();
        order.setUsernameCustomer("testUser");
        orders.add(order);

        when(objectMapper.readValue(tempFileHistoryOrders, new TypeReference<Set<Order>>() {}))
            .thenReturn(orders);

        // Act
        orderRepository.loadHistoryOrders();

        // Assert
        assertTrue(orderRepository.getHistoryOrders().contains(order));
    }

    @Test
    void testCompleteOrder() throws IOException {
        // Arrange
        Order order = new Order();
        order.setUsernameCustomer("testUser");

        // Мокване на записването във файл
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        orderRepository.addOrder(order);

        // Act
        orderRepository.completeOrder(order);

        // Assert
        assertEquals(StatusOrder.COMPLETED, order.getStatusOrder());
        assertTrue(orderRepository.getCompletedOrders().contains(order));
    }

    @Test
    void testStartNewOrder() {
        // Arrange
        String username = "testUser";

        // Act
        orderRepository.startNewOrder(username);

        // Assert
        assertNotNull(orderRepository.getCurrentOrderForUser(username));
    }

    @Test
    void testAddProductToActiveOrder() throws ProductException {
        // Arrange
        String username = "testUser";
        String productKey = "pizza_pepperoni_small";
        int quantity = 1;

        orderRepository.startNewOrder(username);

        // Act
        orderRepository.addProductToActiveOrder(username, productKey, quantity);

        // Assert
        Order order = orderRepository.getCurrentOrderForUser(username);
        assertTrue(order.getOrder().containsKey(productKey));
        assertEquals(quantity, order.getOrder().get(productKey));
    }

    @Test
    void testRemoveFromCurrentOrderForUser() throws ProductException {
        // Arrange
        String username = "testUser";
        String productKey = "pizza_pepperoni_small";
        int quantity = 1;

        orderRepository.startNewOrder(username);
        orderRepository.addProductToActiveOrder(username, productKey, quantity);

        // Act
        orderRepository.removeFromCurrentOrderForUser(username, productKey, quantity);

        // Assert
        Order order = orderRepository.getCurrentOrderForUser(username);
        assertFalse(order.getOrder().containsKey(productKey));
    }

    @Test
    void testFinalizeOrder() throws IOException {
        // Arrange
        String username = "testUser";
        orderRepository.startNewOrder(username);

        // Мокване на записването във файл
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        // Act
        orderRepository.finalizeOrder(username);

        // Assert
        assertTrue(orderRepository.getPendingOrders().contains(orderRepository.getCurrentOrderForUser(username)));
    }

    @Test
    void testGetProfitInPeriod() throws IOException {
        Order order1 = new Order();
//        order1.setOrderDate(LocalDateTime.now().minusDays(1));
//        order1.setTotalPrice(10.0);
//
//        Order order2 = new Order();
//        order2.setOrderDate(LocalDateTime.now().minusDays(2));
//        order2.setTotalPrice(20.0);

        orderRepository.completeOrder(order1);
       // orderRepository.completeOrder(order2);

        LocalDateTime from = LocalDateTime.now().minusDays(3);
        LocalDateTime to = LocalDateTime.now();

        // Act
        double profit = orderRepository.getProfitInPeriod(from, to);

        // Assert
        assertEquals(30.0, profit);
    }

    @Test
    void testIOExceptionDuringSavePendingOrders() throws IOException {
        // Arrange
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doThrow(new IOException("Test IO Exception")).when(objectWriter).writeValue(any(File.class), any());

        // Act & Assert
        try (LogCaptor logCaptor = LogCaptor.forClass(OrderRepository.class)) {
            orderRepository.savePendingOrders();
            assertTrue(logCaptor.getErrorLogs().contains("Error saving pending orders: "));
        }
    }
}