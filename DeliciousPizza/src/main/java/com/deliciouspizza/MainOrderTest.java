package com.deliciouspizza;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.product.Sauce;
import com.deliciouspizza.service.OrderService;
import com.deliciouspizza.utils.DrinkType;
import com.deliciouspizza.utils.DrinkVolume;
import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.deliciouspizza.utils.SauceType;

public class MainOrderTest {

    public static void main(String[] args) {
        Product product1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.MEDIUM);
        Product product2 = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);
        Product product3 = new Sauce(SauceType.GARLIC_SAUCE);

        Product product4 = new Pizza(PizzaType.PEPPERONI, PizzaSize.MEDIUM);
        Product product5 = new Drink(DrinkType.BEER, DrinkVolume.GRANDE);

        Order order = new Order();
        Order order2 = new Order();
        try {
            order.addProduct(product2, 2);
            order.addProduct(product3, 2);

            order2.addProduct(product4, 1);
            order2.addProduct(product5, 3);

        } catch (Exception err) {
            System.out.println(err.getMessage());
        }

        OrderService service = new OrderService();

        service.createOrder(order, "monkata");
        service.createOrder(order2, "daka");

        service.getPendingOrders();

        service.processCurrentOrder();
    }

}
