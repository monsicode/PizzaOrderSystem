package com.deliciouspizza.service;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.utils.StatusOrder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//----------------------------------

//createOrder(List<Product>)   with one or more products
//orderProcessing()
//currentOrder()
//finishedOrders(User) for the user {return unmodify user.getOrderHistory}
//repeatOrder(User) {}

//----------------------------------

public class OrderService {

    private final OrderRepository orderRepository = new OrderRepository();
    //userRepo --> user.json
    private Order currentOrder;

    public Order createOrder(Map<Product, Integer> productsWithQuantities, String addressDelivary) {
        currentOrder = new Order(productsWithQuantities, addressDelivary);
        //orderRepository.saveOrder(currentOrder);
        return currentOrder;
    }

    // Обработва текущата поръчка (маркира я като "приключена" и я запазва)
    public void processCurrentOrder() {
        if (currentOrder != null) {
            currentOrder.setStatusOrder(StatusOrder.COMPLETED);
            //orderRepository.saveOrder(currentOrder); // Презаписваме текущата поръчка като "приключена"
            currentOrder = null; // Освобождаваме текущата поръчка след приключване
        } else {
            throw new IllegalStateException("Няма текуща поръчка за обработка.");
        }
    }

    public List<Order> getFinishedOrders() {
        //return orderRepository.getFinished();
        return null;
    }

    // Връща текущата поръчка, ако има такава
    public Optional<Order> getCurrentOrder() {
        return Optional.ofNullable(currentOrder);
    }

    //should i repeat the order by given order? todo: other way
    public Order repeatOrder(Order order, String addressDelivary) {
        //if( !orderRepository.isOrdererFinished(order)){
        //  throw IllegalArgumentException(This order dosen't exist to be repeated!);
        // }

        // Създаваме нова поръчка със същите продукти
        Order repeatedOrder = new Order(order.getOrder(), addressDelivary);
        // orderRepository.saveOrder(repeatedOrder);
        currentOrder = repeatedOrder;
        return repeatedOrder;
    }

}
