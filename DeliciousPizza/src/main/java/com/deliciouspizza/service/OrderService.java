package com.deliciouspizza.service;

import com.deliciouspizza.exception.ProductException;
import com.deliciouspizza.exception.UnderAgedException;
import com.deliciouspizza.utils.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class OrderService {

    private static final Logger LOGGER = LogManager.getLogger(OrderService.class);

    private final OrderRepository orderRepository = Singleton.getInstance(OrderRepository.class);
    private final UserRepository userRepository = Singleton.getInstance(UserRepository.class);
    private final ProductRepository productRepository = Singleton.getInstance(ProductRepository.class);

    private static final int ADULT_AGE = 18;

    public void startNewOrder(String username) {
        orderRepository.startNewOrder(username);
    }

    //GOOD
    public void addProductToActiveOrder(String username, String productKey, int quantity)
        throws ProductException, UnderAgedException {

        if (isItGoodForUnderAgedCustomers(username, productKey)) {
            orderRepository.addProductToActiveOrder(username, productKey, quantity);
        } else {
            LOGGER.warn("Under aged user {} is trying to order {}", username, productKey);
            throw new UnderAgedException("Sorry , you can't have this drink, you are under aged" + username);
        }
    }

    public void removeFromCurrentOrderForUser(String username, String productKey, Integer quantity)
        throws ProductException {
        orderRepository.removeFromCurrentOrderForUser(username, productKey, quantity);
    }

    public Map<String, Integer> getCurrentOrderForUser(String username) {
        try {
            return orderRepository.getCurrentOrderForUser(username).getOrder();
        } catch (IllegalStateException err) {
            LOGGER.error(err.getMessage(), err);
        }
        return new HashMap<>();
    }

    //for customers
    public void finalizeOrder(String username) {
        orderRepository.finalizeOrder(username);
    }

    public void finalizeRepeatedOrder(Order order) {
        try {
            orderRepository.finalizeRepeatedOrder(order);
        } catch (IllegalStateException err) {
            LOGGER.error(err.getMessage(), err);
        }
    }

    public double getTotalPriceOfOrderForCustomer(String username) {
        return orderRepository.getTotalPriceOfOrder(username);
    }

    //for employees
    public void processCurrentOrder() {
        try {
            Order currentOrder = orderRepository.getNextOrder();
            LOGGER.info("Started processing current order: {} ", currentOrder);

            if (currentOrder != null) {
                orderRepository.completeOrder(currentOrder);
                String username = currentOrder.getUsernameCustomer();
                userRepository.addToOrderHistory(username, currentOrder);
            } else {
                LOGGER.warn("There are no orders to process.");
            }

        } catch (InterruptedException err) {
            Thread.currentThread().interrupt();
            LOGGER.error("Error processing the order {}", err.getMessage(), err);
        }
    }

    public BlockingQueue<Order> getPendingOrders() {
        return orderRepository.getPendingOrders();
    }

    public long getCountOrderInPeriod(LocalDateTime from, LocalDateTime to) {
        return orderRepository.getCountOrderInPeriod(from, to);
    }

    public double getProfitInPeriod(LocalDateTime from, LocalDateTime to) {
        return orderRepository.getProfitInPeriod(from, to);
    }

    //to optimize -> check if its a drink? is it bad i have Drink class here ?
    private boolean isItGoodForUnderAgedCustomers(String username, String productKey) {
        return userRepository.getAgeCustomer(username) > ADULT_AGE ||
            productRepository.isItGoodForUnderAgedCustomers(productKey);

    }

}
