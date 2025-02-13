package com.deliciouspizza.service;

import com.deliciouspizza.exception.ProductException;
import com.deliciouspizza.exception.UnderAgedException;
import com.deliciouspizza.utils.Singleton;
import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.repository.OrderRepository;
import com.deliciouspizza.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class OrderService {

    private static final Logger LOGGER = LogManager.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    private static final int ADULT_AGE = 18;
    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";

    public OrderService() {
        orderRepository = Singleton.getInstance(OrderRepository.class);
        userRepository = Singleton.getInstance(UserRepository.class);
        productService = Singleton.getInstance(ProductService.class);
    }

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    public void startNewOrder(String username) {
        orderRepository.startNewOrder(username);
    }

    //GOOD
    public void addProductToActiveOrder(String username, String productKey, int quantity)
        throws ProductException, UnderAgedException {

        if (isItGoodForUnderAgedCustomers(username, productKey)) {
            orderRepository.addProductToActiveOrder(username, productKey, quantity);
        } else {
            LOGGER.warn("Under aged user {} is trying to order {}", username, productKey);
            throw new UnderAgedException("Sorry, you can't have this drink, you are under aged " + username);
        }
    }

    public void removeFromCurrentOrderForUser(String username, String productKey, Integer quantity)
        throws ProductException {
        orderRepository.removeFromCurrentOrderForUser(username, productKey, quantity);
    }

    public Map<String, Integer> getCurrentOrderForUser(String username) {
        try {
            return orderRepository.getCurrentOrderForUser(username).getOrder();
        } catch (IllegalStateException err) {
            LOGGER.error(err.getMessage());
        }
        return new HashMap<>();
    }

    //for customers
    public void finalizeOrder(String username) {
        orderRepository.finalizeOrder(username);
    }

    public void finalizeRepeatedOrder(Order order) {
        try {
            orderRepository.finalizeRepeatedOrder(order);
        } catch (IllegalStateException err) {
            LOGGER.error(err.getMessage(), err);
        }
    }

    public double getTotalPriceOfOrderForCustomer(String username) {
        return orderRepository.getTotalPriceOfOrder(username);
    }

    public String getUserForCurrentOrder() {
        return orderRepository.getUserForNextOrder();
    }

    //for employees  check if its an employee
    public void processCurrentOrder() throws InterruptedException {

        Order currentOrder = orderRepository.getNextOrder();
        LOGGER.info("Started processing current order: {} ", currentOrder);

        if (currentOrder != null) {
            orderRepository.completeOrder(currentOrder);
            String username = currentOrder.getUsernameCustomer();
            userRepository.addToOrderHistory(username, currentOrder);
        } else {
            LOGGER.warn("There are no orders to process.");
        }

    }

    public BlockingQueue<Order> getPendingOrders() {
        return orderRepository.getPendingOrders();
    }

    public List<Order> getHistoryOfOrders() {
        return orderRepository.getHistoryOrders();
    }

    public long getCountOrderInPeriod(LocalDateTime from, LocalDateTime to) {
        return orderRepository.getCountOrderInPeriod(from, to);
    }

    public double getProfitInPeriod(LocalDateTime from, LocalDateTime to) {
        return orderRepository.getProfitInPeriod(from, to);
    }

    //to optimize -> check if its a drink? is it bad i have Drink class here ?
    private boolean isItGoodForUnderAgedCustomers(String username, String productKey) {
        return userRepository.getAgeCustomer(username) > ADULT_AGE ||
            productService.isItGoodForUnderAgedCustomers(productKey);

    }

    public String showCurrentOrderForUser(String username) {
        Map<String, Integer> orderMap = getCurrentOrderForUser(username);
        StringBuilder orderBuilder = new StringBuilder();

        orderBuilder.append("Your order contains:\n");
        orderBuilder.append("-----------------------------------\n");

        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            String productKey = entry.getKey();
            String product = productKey.replaceAll("_", " ");
            product = product.substring(0, 1).toUpperCase() + product.substring(1).toLowerCase();
            int quantity = entry.getValue();
            double price = productService.getProductPriceByKey(productKey) * quantity;

            orderBuilder.append(String.format(YELLOW + " - " + RESET + " %-20s Quantity: %-2d  Price: $%.2f\n",
                product, quantity, price));
        }

        orderBuilder.append("-----------------------------------\n");
        orderBuilder.append(String.format("Total Price: " + YELLOW + "$%.2f\n" + RESET,
            getTotalPriceOfOrderForCustomer(username)));

        return orderBuilder.toString();
    }

    public String getDeliveryAddress(String username) {
        return userRepository.getAddressCustomer(username);
    }

}
