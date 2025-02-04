package com.deliciouspizza.repository;

import com.deliciouspizza.exception.ErrorInProductNameException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.exception.ProductException;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.exception.InactiveProductException;
import com.deliciouspizza.exception.ProductNotInOrderException;
import com.deliciouspizza.enums.StatusOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OrderRepository {

    private static final Logger LOGGER = LogManager.getLogger(OrderRepository.class);

    private final BlockingQueue<Order> pendingOrders;
    private final Set<Order> historyOrders = ConcurrentHashMap.newKeySet();

    private static final String FILE_PATH_ORDERS = "src/main/resources/pendingOrders.json";
    private static final String FILE_PATH_HISTORY_ORDERS = "src/main/resources/historyOrders.json";
    private static final int DAYS_IN_WEEK = 7;

    private final ObjectMapper objectMapper;

    private final File jsonFile = new File(FILE_PATH_ORDERS);
    private final File historyJsonFile = new File(FILE_PATH_HISTORY_ORDERS);

    private final Map<String, Order> activeOrdersForCustomers = new ConcurrentHashMap<>();

    public OrderRepository() {
        pendingOrders = new LinkedBlockingQueue<>();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();

        historyOrders.addAll(loadHistoryOrders());
        loadPendingOrders();
    }

    public void addOrder(Order order) {
        pendingOrders.add(order);
        savePendingOrders();
        LOGGER.info("Order added to the queue");
    }

    public Order getNextOrder() throws InterruptedException {
        Order order = pendingOrders.take();
        savePendingOrders();
        LOGGER.info("Order retrieved from queue: {}", order);
        return order;
    }

    private synchronized void loadPendingOrders() {
        try {
            if (jsonFile.exists() && jsonFile.length() > 0) {
                BlockingQueue<Order> loadedOrders =
                    objectMapper.readValue(jsonFile, new TypeReference<LinkedBlockingQueue<Order>>() {
                    });
                pendingOrders.addAll(loadedOrders);
            }
        } catch (IOException e) {
            LOGGER.error("Error loading pending orders: ", e);
        }
    }

    private synchronized Set<Order> loadHistoryOrders() {
        try {
            if (historyJsonFile.exists() && historyJsonFile.length() > 0) {
                return objectMapper.readValue(historyJsonFile, new TypeReference<Set<Order>>() {
                });
            }
        } catch (IOException e) {
            LOGGER.error("Error loading completed orders history: ", e);
        }
        return new HashSet<>();
    }

    private synchronized void savePendingOrders() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, pendingOrders);
        } catch (IOException e) {
            LOGGER.error("Error saving pending orders: ", e);
        }
    }

    private synchronized void saveHistoryOrders() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(historyJsonFile, historyOrders);
            LOGGER.info("Order history saved successfully.");
        } catch (IOException e) {
            LOGGER.error("Error saving completed orders history: ", e);
        }
    }

    public BlockingQueue<Order> getPendingOrders() {
        return pendingOrders;
    }

    public List<Order> getHistoryOrders() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(DAYS_IN_WEEK);

        return historyOrders.stream()
            .filter(order -> order.getOrderDate().isAfter(oneWeekAgo))
            .toList();
    }

    public void completeOrder(Order order) {
        order.setStatusOrder(StatusOrder.COMPLETED);
        historyOrders.add(order);
        saveHistoryOrders();
        savePendingOrders();

        LOGGER.info("Order completed: {}", order);
    }

    public synchronized void shutdown() {
        savePendingOrders();
    }

    public void startNewOrder(String username) {

        if (activeOrdersForCustomers.putIfAbsent(username, new Order()) != null) {
            throw new IllegalStateException("User already has an active order. Finish it before starting a new one.");
        }

        Order newOrder = activeOrdersForCustomers.get(username);

        if (newOrder != null) {
            newOrder.setUsernameCustomer(username);
        }

        LOGGER.info("New order started for user: {}", username);
    }

    public void addProductToActiveOrder(String username, String productKey, int quantity) throws ProductException {
        Order order = activeOrdersForCustomers.get(username);

        if (order == null) {
            throw new IllegalStateException("User does not have an active order. Start an order first.");
        }

        try {
            activeOrdersForCustomers.get(username).addProduct(productKey, quantity);
            LOGGER.info("Added product {} to order for user {}: quantity {}", productKey, username, quantity);
        } catch (InactiveProductException | IllegalArgumentException | ProductDoesNotExistException |
                 ErrorInProductNameException e) {
            throw new ProductException(e.getMessage());
        }
    }

    public Order getCurrentOrderForUser(String username) {
        Order order = activeOrdersForCustomers.get(username);
        if (order == null) {
            throw new IllegalStateException("User does not have an order.");
        }

        if (order.getOrder().isEmpty()) {
            LOGGER.warn("Current order is empty for user {}: {}", username, order);
        }

        return order;
    }

    public void removeFromCurrentOrderForUser(String username, String productKey, Integer quantity)
        throws ProductException {
        Order order = activeOrdersForCustomers.get(username);
        if (order == null) {
            throw new IllegalStateException("User does not have an active order to finalize.");
        }

        if (order.getOrder().isEmpty()) {
            throw new IllegalStateException("Current order is empty");
        }

        try {
            order.removeProduct(productKey, quantity);
            LOGGER.info("Removed product {} from order for user {}: quantity {}", productKey, username, quantity);
        } catch (ProductNotInOrderException | IllegalArgumentException | ProductDoesNotExistException err) {
            throw new ProductException(err.getMessage());
        }
    }

    public void finalizeOrder(String username) {
        Order order = activeOrdersForCustomers.get(username);

        if (order == null) {
            throw new IllegalStateException("User does not have an active order to finalize.");
        }

        if (order.getOrder().isEmpty()) {
            throw new IllegalStateException("Order cannot be finalized as it has no products.");
        }

        activeOrdersForCustomers.remove(username);
        addOrder(order);
        LOGGER.info("Order finalized for user {}: {}", username, order);
    }

    public Set<Order> getCompletedOrders() {
        return new HashSet<>(historyOrders);
    }

    public long getCountOrderInPeriod(LocalDateTime from, LocalDateTime to) {
        long count = 0;

        for (Order order : historyOrders) {
            LocalDateTime orderDate = order.getOrderDate();

            if (!orderDate.isBefore(from) && !orderDate.isAfter(to)) {
                count++;
            }
        }

        return count;
    }

    public double getProfitInPeriod(LocalDateTime from, LocalDateTime to) {
        double totalProfit = 0;

        for (Order order : historyOrders) {
            double curOrderPrice = order.getTotalPrice();
            LocalDateTime orderDate = order.getOrderDate();

            if (!orderDate.isBefore(from) && !orderDate.isAfter(to)) {
                totalProfit += curOrderPrice;
            }
        }

        return BigDecimal.valueOf(totalProfit)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
    }

    public double getTotalPriceOfOrder(String username) {
        try {
            Order order = getCurrentOrderForUser(username);
            return order.getTotalPrice();
        } catch (IllegalStateException err) {
            System.out.println(err.getMessage());
        }
        return 0;
    }

    public void finalizeRepeatedOrder(Order order) {

        if (order == null) {
            throw new IllegalStateException("Order doesn't exit to be repeated");
        }

        addOrder(order);
        LOGGER.info("Order repeated successfully: {}", order);
    }

}
