package com.deliciouspizza;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.product.Sauce;
import com.deliciouspizza.entity.user.Customer;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.service.OrderService;
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
    public static void main(String[] args) {

//       ProductRepository repository = new ProductRepository();
//        ProductRepository repository = Singleton.getInstance(ProductRepository.class);
//        Product pizza1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
//        Product pizza2 = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
//        Product pizza3 = new Pizza(PizzaType.PEPPERONI, PizzaSize.LARGE);
//        Product drink = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);
//        Product drink2 = new Drink(DrinkType.BEER, DrinkVolume.GRANDE);
//
//        Product test = new Pizza(PizzaType.SEAFOOD, PizzaSize.SMALL);
//
//        repository.deactivateProduct(test);
//
//        repository.addProduct(test);
//        repository.addProduct(pizza1);
//        repository.addProduct(pizza2);
//        repository.addProduct(pizza3);
//        repository.addProduct(drink);
//        repository.addProduct(drink2);


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
        // Създаване на OrderRepository
        OrderRepository orderRepository = new OrderRepository();

        // Създаване на продукти и добавяне в поръчка
        Product pizza = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
        Product drink = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);
        Product pizza2 = new Pizza(PizzaType.PEPPERONI, PizzaSize.LARGE);

        Map<String, Integer> productsWithQuantities1 = new HashMap<>();
        productsWithQuantities1.put(pizza.generateKey(), 2);
        productsWithQuantities1.put(drink.generateKey(), 1);

        Map<String, Integer> productsWithQuantities2 = new HashMap<>();
        productsWithQuantities2.put(pizza.generateKey(), 1);
        productsWithQuantities2.put(drink.generateKey(), 2);

        Map<String, Integer> productsWithQuantities3 = new HashMap<>();
        productsWithQuantities3.put(pizza2.generateKey(), 4);
        productsWithQuantities3.put(drink.generateKey(), 2);

        Order order1 = new Order(productsWithQuantities1, "monkata");
        Order order2 = new Order(productsWithQuantities2, "daka");
        Order order3 = new Order(productsWithQuantities2, "Dupe");

        //System.out.println(order1.getTotalPrice());

        orderRepository.addOrder(order1);
        orderRepository.addOrder(order2);

        BlockingQueue<Order> pendingOrders = orderRepository.getPendingOrders();
//        System.out.println("Pending Orders:");
//        pendingOrders.forEach(System.out::println);

        // Обработка на поръчка
        try {
            Order nextOrder = orderRepository.getNextOrder();
            System.out.println("Processing order: " + nextOrder);
        } catch (InterruptedException e) {
            System.out.println("Error with getNextOrder()");
        }

//        orderRepository.addOrder(order3);

//
//        Customer petkan = new Customer("petkan123", "1234", "Baba Yaga 37",27);
//
//        OrderService service = new OrderService();
//        service.createOrder(productsWithQuantities1, petkan);
//
//        System.out.println("Orders loaded from JSON:");
//        service.getPendingOrders().forEach(System.out::println);
//
////        service.processCurrentOrder();
////
////        System.out.println("Orders loaded from JSON:");
////        orderRepository.getPendingOrders().forEach(System.out::println);
////    }
    }
}
