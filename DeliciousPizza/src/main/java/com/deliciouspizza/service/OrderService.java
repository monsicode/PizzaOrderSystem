package com.deliciouspizza.service;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.exception.InactiveProductException;
import com.deliciouspizza.repository.OrderRepository;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

//----------------------------------
//repeatOrder(User) {}
//userRepo --> user.json
//----------------------------------

public class OrderService {

    private static final OrderRepository ORDER_REPOSITORY = Singleton.getInstance(OrderRepository.class);

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
            System.out.println("Order processing started:  " + currentOrder);

            if (currentOrder != null) {
                ORDER_REPOSITORY.completeOrder(currentOrder);
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

//    public Set<Order> getFinishedOrders(String username) {
//        return USER_REPOSITORY.getOrderHistory(username);
//    }

}
