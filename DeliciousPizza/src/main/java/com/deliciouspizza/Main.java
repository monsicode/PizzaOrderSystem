package com.deliciouspizza;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.product.Sauce;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.utils.DrinkType;
import com.deliciouspizza.utils.DrinkVolume;
import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.deliciouspizza.utils.SauceType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Main {

    //main test for ProductService and Repository
//    public static void main(String[] args) {
//
//        ProductRepository repository = new ProductRepository();
//
//        Product pizza1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
//        Product pizza2 = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
//        Product pizza3 = new Pizza(PizzaType.PEPPERONI, PizzaSize.LARGE);
//        Product drink = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);
//        Product drink2 = new Drink(DrinkType.BEER, DrinkVolume.GRANDE);
//
//        repository.addProduct(pizza1);
//        repository.addProduct(pizza2);
//        repository.addProduct(pizza3);
//        repository.addProduct(drink);
//        repository.addProduct(drink2);
//
//        //---------------------------------------------------
//
//        ProductService service = new ProductService(repository);
//
//        service.deactivateProduct(pizza1);
//        System.out.println(service.getActiveProducts());
//
//        Product sauce = new Sauce(SauceType.BBQ_SAUCE);
//
//        service.addNewProduct(sauce);
//
//        System.out.println(service.getProductPrice(pizza1));
//        System.out.println(service.getProductPrice(drink));
//
//        //----------------------------------------------------
//
//    }

    public static void main(String[] args) {
        // Създаване на OrderRepository
        OrderRepository orderRepository = new OrderRepository();

        // Създаване на продукти и добавяне в поръчка
        Product pizza = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
        Product drink = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);

        Map<Product, Integer> productsWithQuantities1 = new HashMap<>();
        productsWithQuantities1.put(pizza, 2);
        productsWithQuantities1.put(drink, 1);

        Order order1 = new Order(productsWithQuantities1, "123 Main St.");

        Map<Product, Integer> productsWithQuantities2 = new HashMap<>();
        productsWithQuantities2.put(pizza, 1);
        productsWithQuantities2.put(drink, 2);

        Order order2 = new Order(productsWithQuantities2, "456 Elm St.");

        orderRepository.addOrder(order1);
        orderRepository.addOrder(order2);

        //BlockingQueue<Order> pendingOrders = orderRepository.getPendingOrders();
//        System.out.println("Pending Orders:");
//        pendingOrders.forEach(System.out::println);

        // Обработка на поръчка
        try {
            Order nextOrder = orderRepository.getNextOrder();
            System.out.println("Processing order: " + nextOrder);
        } catch (InterruptedException e) {
            System.out.println("Error with getNextOrder()");
        }
//
//        System.out.println("Orders loaded from JSON:");
//        orderRepository.getPendingOrders().forEach(System.out::println);
//    }
    }
}
