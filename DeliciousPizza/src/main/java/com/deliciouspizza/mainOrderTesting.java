//package com.deliciouspizza;
//
//import com.deliciouspizza.entity.order.Order;
//import com.deliciouspizza.entity.product.Pizza;
//import com.deliciouspizza.entity.product.Product;
//import com.deliciouspizza.entity.user.Customer;
//import com.deliciouspizza.entity.user.User;
//import com.deliciouspizza.utils.PizzaSize;
//import com.deliciouspizza.utils.PizzaType;
//
//public class mainOrderTesting {
//    public static void main(String[] args) {
//        Product pizza1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
//        Product pizza2 = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
//
//        User user1 = new Customer("monkata", "password123", "ул. Пиротска 10", 25);
//
//        Order order = new Order("monkata");
//
//        order.addProduct(pizza1,2);
//        order.addProduct(pizza2,4);
//
//        System.out.println(order.getTotalPrice());
//        System.out.println(order.getOrder());
//
//
//    }
//}
