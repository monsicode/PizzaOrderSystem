package com.deliciouspizza.mains;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.product.Sauce;
import com.deliciouspizza.enums.DrinkType;
import com.deliciouspizza.enums.DrinkVolume;
import com.deliciouspizza.enums.PizzaSize;
import com.deliciouspizza.enums.PizzaType;
import com.deliciouspizza.enums.SauceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainOrderTest {
    private static final Logger LOGGER = LogManager.getLogger(MainOrderTest.class);
    public static void main(String[] args) {

        Product product1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.MEDIUM);
//        Product product2 = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);
        Product product3 = new Sauce(SauceType.GARLIC_SAUCE);

        Product product4 = new Pizza(PizzaType.PEPPERONI, PizzaSize.MEDIUM);
//        Product product5 = new Drink(DrinkType.BEER, DrinkVolume.GRANDE);

        Order order = new Order();
        Order order2 = new Order();
        Order orde5 = new Order();
        Order order6 = new Order();
        Order order9 = new Order();

        try {
            //order.addProduct("sauce_garlic_sauce", 2);
            order.addProduct("pizza_pepperoni_small", 2);

           // order.removeProduct("sauce_garlic_sauce", 1);
            order.removeProduct("pizza_pepperoni_small", 1);

        } catch (Exception err) {
            LOGGER.error("An error occurred: {}", err.getMessage(), err);
        }

//        System.out.println(order);
//        System.out.println(order.getTotalPrice());

//        OrderService service = new OrderService();
//
//        service.createOrder(order, "monkata");
//        service.createOrder(order2, "daka");
//
//        service.getPendingOrders();
//
//        service.processCurrentOrder();
    }

}
