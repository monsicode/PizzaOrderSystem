package com.deliciouspizza.service;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.utils.StatusOrder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

//----------------------------------

//createOrder(List<Product>)   with one or more products --> ОК
//orderProcessing() --> ОК
//currentOrder() --> ok
//finishedOrders(User) for the user {return unmodify user.getOrderHistory}
//repeatOrder(User) {}
//changeAddressOfDelivery -- ok
//userRepo --> user.json
//----------------------------------

public class OrderService {
    private final OrderRepository orderRepository = new OrderRepository();
    private Order currentOrder;

    public void createOrder(Map<Product, Integer> productsWithQuantities, Customer customer) {
        Order order = new Order(productsWithQuantities, customer.getUsername());
        order.setAddressDelivery(customer.getAddress());
        orderRepository.addOrder(order);
    }

    public void processCurrentOrder() {
        try {
            Order currentOrder = orderRepository.getNextOrder();
            System.out.println("Обработка на поръчка започна: " + currentOrder);

            if (currentOrder != null) {
                orderRepository.completeOrder(currentOrder);
            } else {
                System.out.println("Няма поръчки за обработка.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Грешка при обработката на поръчката: " + e.getMessage());
        }
    }

    //dosent look okey --> to remove or modify
    public void changeAddressOfDelivery(Customer customer, String newAddress) {
        customer.setAddress(newAddress);
    }

    public BlockingQueue<Order> getPendingOrders() {
        return orderRepository.getPendingOrders();  // Връща текущите чакащи поръчки
    }

}
