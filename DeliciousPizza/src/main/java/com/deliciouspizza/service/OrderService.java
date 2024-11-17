package com.deliciouspizza.service;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.repository.UserRepository;

import java.time.LocalDateTime;
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


    private final Map<String, Order> activeOrders = new ConcurrentHashMap<>();

    public void startNewOrder(String username) {
        try {
            ORDER_REPOSITORY.startNewOrder(username);
        } catch (IllegalStateException err) {
            System.out.println(err.getMessage());
        }
    }

    public void addProductToActiveOrder(String username, String productKey, int quantity) {
        try {
            ORDER_REPOSITORY.addProductToActiveOrder(username, productKey, quantity);
        } catch (IllegalStateException e) {
            System.out.println("Cannot add product to order: " + e.getMessage());
        }
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

//    public Set<Order> getFinishedOrders(String username) {
//        return USER_REPOSITORY.getOrderHistory(username);
//    }

}
