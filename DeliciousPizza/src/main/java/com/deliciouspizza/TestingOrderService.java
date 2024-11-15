package com.deliciouspizza;

import com.deliciouspizza.service.OrderService;

public class TestingOrderService {

    public static void main(String[] args) {
        OrderService orderService = new OrderService();

        System.out.println("Starting new order for customer1...");
        orderService.startNewOrder("customer1");

        System.out.println("Adding product to order...");
        orderService.addProductToActiveOrder("customer1", "sauce_garlic_sauce", 2);
        orderService.addProductToActiveOrder("customer1", "drink_coke", 3);

        System.out.println("Finalizing order for customer1...");
        orderService.finalizeOrder("customer1");

        System.out.println("Employee starts processing orders...");
        orderService.processCurrentOrder();

    }
}
