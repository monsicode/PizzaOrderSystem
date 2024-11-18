package com.deliciouspizza.service;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.exception.ErrorInProductNameException;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

//----------------------------------
//repeatOrder(User) {}
//userRepo --> user.json
//----------------------------------

public class OrderService {

    private static final OrderRepository ORDER_REPOSITORY = Singleton.getInstance(OrderRepository.class);
    private static final UserRepository USER_REPOSITORY = Singleton.getInstance(UserRepository.class);
    private static final ProductRepository PRODUCT_REPOSITORY = Singleton.getInstance(ProductRepository.class);

    private static final int ADULT_AGE = 18;

    private final Map<String, Order> activeOrders = new ConcurrentHashMap<>();

    public void startNewOrder(String username) {
        try {
            ORDER_REPOSITORY.startNewOrder(username);
        } catch (IllegalStateException err) {
            System.out.println(err.getMessage());
        }
    }

    public void addProductToActiveOrder(String username, String productKey, int quantity)
        throws ErrorInProductNameException {
        try {
            if (isItGoodForUnderAgedCustomers(username, productKey)) {
                ORDER_REPOSITORY.addProductToActiveOrder(username, productKey, quantity);
            } else {
                System.out.println("Sorry, you can't have this drink, you are under aged");
            }

        } catch (IllegalStateException | IllegalArgumentException e) {
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

    //for employees
    public void processCurrentOrder() {
        try {
            Order currentOrder = ORDER_REPOSITORY.getNextOrder();
            System.out.println("Order processing started:");

            if (currentOrder != null) {
                ORDER_REPOSITORY.completeOrder(currentOrder);
                String username = currentOrder.getUsernameCustomer();
                USER_REPOSITORY.addToOrderHistory(username, currentOrder);
            } else {
                System.out.println("There are no orders to process.");
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

//    public Set<Order> getFinishedOrders(String username) {
//        return USER_REPOSITORY.getOrderHistory(username);
//    }

}
