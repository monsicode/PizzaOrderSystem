package com.deliciouspizza.service;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.repository.UserRepository;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

//----------------------------------

//createOrder(List<Product>)   with one or more products --> ОК
//orderProcessing() --> ОК
//currentOrder() --> ok
//finishedOrders(User) for the user {return unmodify user.getOrderHistory}
//repeatOrder(User) {}
//userRepo --> user.json
//----------------------------------

public class OrderService {

    private static final OrderRepository ORDER_REPOSITORY = Singleton.getInstance(OrderRepository.class);
    private static final UserRepository USER_REPOSITORY = Singleton.getInstance(UserRepository.class);

    //removed Map<> as param
    public void createOrder(Map<String, Integer> productsWithQuantities, Customer customer) {
        Order order = new Order(productsWithQuantities, customer.getUsername());
        ORDER_REPOSITORY.addOrder(order);
    }

    public void createOrder(Order order, String usernameCustomer) {
        order.setUsernameCustomer(usernameCustomer);
        ORDER_REPOSITORY.addOrder(order);
    }

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

    public Set<Order> getFinishedOrders(String username) {
        return USER_REPOSITORY.getOrderHistory(username);
    }

}
