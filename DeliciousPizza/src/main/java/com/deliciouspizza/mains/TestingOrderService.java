package com.deliciouspizza.mains;

import com.deliciouspizza.service.OrderService;

public class TestingOrderService {

    public static void main(String[] args) {
        OrderService orderService = new OrderService();

        System.out.println("Starting new order for customer2...");

        orderService.startNewOrder("customer2");
        orderService.startNewOrder("customer3");

        System.out.println("Adding product to order...");

        orderService.addProductToActiveOrder("customer2", "pizza_pepperoni", 2);
        orderService.addProductToActiveOrder("customer2", "drink_coke", 1);

        orderService.addProductToActiveOrder("customer3", "pizza_pepperoni", 2);

        System.out.println("Finalizing order for customers...");

        orderService.finalizeOrder("customer2");
        orderService.finalizeOrder("customer3");

        System.out.println("Employee starts processing orders...");
        orderService.processCurrentOrder();
        orderService.processCurrentOrder();

    }
}
