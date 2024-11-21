package com.deliciouspizza.mains;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.entity.user.User;
import com.deliciouspizza.enums.PizzaSize;
import com.deliciouspizza.enums.PizzaType;

import java.util.HashMap;
import java.util.Map;

public class MainOrderTesting {

    public static void main(String[] args) {

        Product pizza1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.MEDIUM);
        Product pizza2 = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);

        User user1 = new Customer("random", "123", "ул. Nqkva 10", 40);
        Order order = new Order("random");

        Map<Integer, Order> ordersHistory = new HashMap<>();

        ordersHistory.put(1, order);

    }
}
