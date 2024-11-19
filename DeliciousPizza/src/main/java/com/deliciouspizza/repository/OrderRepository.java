package com.deliciouspizza.repository;

import com.deliciouspizza.Singleton;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//-----------------
//За бързодействие --> база данни или in-memory storage(при рестарт вс изчезва)
//-->периодично записване на поръчките, на всеки 5-10 мин или при затваряне на приложението
//-----------------
//finishOrderForUser(User, Order);
//savePendingOrder(Order) --> save pending order to the file
//loadPendingOrders()
//за периодично записване
//private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//-----------------

public class OrderRepository {
    private final BlockingQueue<Order> pendingOrders;
    private final Set<Order> historyOrders = ConcurrentHashMap.newKeySet();

    private static final ProductRepository PRODUCT_REPOSITORY = Singleton.getInstance(ProductRepository.class);

    private static final String FILE_PATH_ORDERS = "src/main/resources/pendingOrders.json";
    private static final String FILE_PATH_HISTORY_ORDERS = "src/main/resources/historyOrders.json";

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
    }

    public Order getNextOrder() throws InterruptedException {
        Order order = pendingOrders.take();
        savePendingOrders();
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
            System.err.println("Error loading orders: " + e.getMessage());
        }
    }

    private synchronized Set<Order> loadHistoryOrders() {
        try {
            if (historyJsonFile.exists() && historyJsonFile.length() > 0) {
                return objectMapper.readValue(historyJsonFile, new TypeReference<Set<Order>>() {
                });
            }
        } catch (IOException e) {
            System.err.println("Error loading completed orders: " + e.getMessage());
        }
        return new HashSet<>();
    }

    private synchronized void savePendingOrders() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, pendingOrders);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving the orders!");
        }
    }

    private synchronized void saveHistoryOrders() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(historyJsonFile, historyOrders);
            System.out.println("Order history saved to file.");
        } catch (IOException e) {
            System.err.println("Error saving the completed order history: " + e.getMessage());
        }
    }

    public BlockingQueue<Order> getPendingOrders() {
        return pendingOrders;
    }

    public void completeOrder(Order order) {
        order.setStatusOrder(StatusOrder.COMPLETED);
        historyOrders.add(order);
        saveHistoryOrders();
        System.out.println("Order completed! ");
        savePendingOrders();
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

        System.out.println("New order started for user: " + username);
    }

    public void addProductToActiveOrder(String username, String productKey, int quantity) {
        Order order = activeOrdersForCustomers.get(username);

        if (order == null) {
            throw new IllegalStateException("User does not have an active order. Start an order first.");
        }

        //check if product exist

        try {
            //order.addProduct(productKey, quantity);
            activeOrdersForCustomers.get(username).addProduct(productKey, quantity);
        } catch (InactiveProductException | IllegalArgumentException e) {
            System.out.println("Cannot add product to order: " + e.getMessage());
        }
    }

    public Order getCurrentOrderForUser(String username) {
        Order order = activeOrdersForCustomers.get(username);
        if (order == null) {
            throw new IllegalStateException("User does not have an order.");
        }

        if (order.getOrder().isEmpty()) {
            System.out.println("Order is empty.");
        }

        return order;
    }

    public void removeFromCurrentOrderForUser(String username, String productKey, Integer quantity) {
        Order order = activeOrdersForCustomers.get(username);
        if (order == null) {
            throw new IllegalStateException("User does not have an active order to finalize.");
        }

        if (PRODUCT_REPOSITORY.isProductActive(productKey)) {

        }

        if (order.getOrder().isEmpty()) {
            System.out.println("Order is empty.");
        }

        try {
            order.removeProduct(productKey, quantity);
        } catch (ProductNotInOrderException | IllegalArgumentException err) {
            System.out.println(err.getMessage());
        }

    }

    public void finalizeOrder(String username) {
        Order order = activeOrdersForCustomers.remove(username);

        if (order == null) {
            throw new IllegalStateException("User does not have an active order to finalize.");
        }

        if (order.getOrder().isEmpty()) {
            throw new IllegalStateException("Order cannot be finalized as it has no products.");
        }

        addOrder(order);
        System.out.println("Order finalized and saved for user: " + username);
        System.out.println(order);
    }

    public Set<Order> getCompletedOrders() {
        return new HashSet<>(historyOrders); // Връщаме копие за безопасност
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

}
