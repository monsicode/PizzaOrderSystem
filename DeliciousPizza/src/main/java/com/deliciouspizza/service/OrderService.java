package com.deliciouspizza.service;

import com.deliciouspizza.utils.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.exception.ErrorInProductNameException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {

    private static final Logger LOGGER = LogManager.getLogger(OrderService.class);

    private static final OrderRepository ORDER_REPOSITORY = Singleton.getInstance(OrderRepository.class);
    private static final UserRepository USER_REPOSITORY = Singleton.getInstance(UserRepository.class);
    private static final ProductRepository PRODUCT_REPOSITORY = Singleton.getInstance(ProductRepository.class);

    private static final int ADULT_AGE = 18;

    private final Map<String, Order> activeOrders = new ConcurrentHashMap<>();

    public void startNewOrder(String username) {
//        try {
//            ORDER_REPOSITORY.startNewOrder(username);
//        } catch (IllegalStateException err) {
//            System.out.println(err.getMessage());
//        }
        ORDER_REPOSITORY.startNewOrder(username);
    }

    public void addProductToActiveOrder(String username, String productKey, int quantity)
        throws ErrorInProductNameException {
        try {
            if (isItGoodForUnderAgedCustomers(username, productKey)) {
                ORDER_REPOSITORY.addProductToActiveOrder(username, productKey, quantity);
            } else {
                LOGGER.warn("Sorry {}, you can't have this drink, you are under aged", username);
            }

        } catch (IllegalStateException | ProductDoesNotExistException e) {
            throw new ErrorInProductNameException(e.getMessage(), e);
        }
    }

    public void removeFromCurrentOrderForUser(String username, String productKey, Integer quantity) {
        try {
            ORDER_REPOSITORY.removeFromCurrentOrderForUser(username, productKey, quantity);
        } catch (IllegalStateException err) {
            System.out.println(err.getMessage());
        }
    }

    public Map<String, Integer> getCurrentOrderForUser(String username) {
        try {
            return ORDER_REPOSITORY.getCurrentOrderForUser(username).getOrder();
        } catch (IllegalStateException err) {
            System.out.println(err.getMessage());
        }
        return new HashMap<>();
    }

    //for customers
    public void finalizeOrder(String username) {
        try {
            ORDER_REPOSITORY.finalizeOrder(username);
        } catch (IllegalStateException err) {
            System.out.println(err.getMessage());
        }
    }

    public void finalizeRepeatedOrder(Order order) {
        try {
            ORDER_REPOSITORY.finalizeRepeatedOrder(order);
        } catch (IllegalStateException err) {
            System.out.println(err.getMessage());
        }
    }

    public double getTotalPriceOfOrderForCustomer(String username) {
        return ORDER_REPOSITORY.getTotalPriceOfOrder(username);
    }

    //for employees
    public void processCurrentOrder() {
        try {
            Order currentOrder = ORDER_REPOSITORY.getNextOrder();
            LOGGER.info("Started processing current order: {} ", currentOrder);

            if (currentOrder != null) {
                ORDER_REPOSITORY.completeOrder(currentOrder);
                String username = currentOrder.getUsernameCustomer();
                USER_REPOSITORY.addToOrderHistory(username, currentOrder);
            } else {
                LOGGER.warn("There are no orders to process.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error processing the order " + e.getMessage());
        }
    }

    public BlockingQueue<Order> getPendingOrders() {
        return ORDER_REPOSITORY.getPendingOrders();
    }

    public long getCountOrderInPeriod(LocalDateTime from, LocalDateTime to) {
        return ORDER_REPOSITORY.getCountOrderInPeriod(from, to);
    }

    public double getProfitInPeriod(LocalDateTime from, LocalDateTime to) {
        return ORDER_REPOSITORY.getProfitInPeriod(from, to);
    }

    private boolean isItGoodForUnderAgedCustomers(String username, String productKey) {
        return USER_REPOSITORY.getAgeCustomer(username) > ADULT_AGE ||
            PRODUCT_REPOSITORY.isItGoodForUnderAgedCustomers(productKey);
    }

}
