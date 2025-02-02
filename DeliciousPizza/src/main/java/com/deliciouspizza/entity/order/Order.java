package com.deliciouspizza.entity.order;

import com.deliciouspizza.exception.ErrorInProductNameException;
import com.deliciouspizza.repository.Warehouse;
import com.deliciouspizza.utils.Singleton;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.InactiveProductException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.exception.ProductNotInOrderException;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.enums.StatusOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    private static final Logger LOGGER = LogManager.getLogger(Order.class);

    private static int idCounter = 0;
    private final int id;

    private final Map<String, Integer> order;
    private StatusOrder statusOrder;
    private LocalDateTime orderDate;
    private double totalPrice;
    private String usernameCustomer;

    private final ProductRepository productRepository = Singleton.getInstance(ProductRepository.class);
    private final Warehouse warehouse = Singleton.getInstance(Warehouse.class);

    public Order() {
        synchronized (Order.class) {
            this.id = ++idCounter;
        }
        this.orderDate = LocalDateTime.now();
        this.statusOrder = StatusOrder.PROCESSING;
        order = new HashMap<>();
    }

    public Order(Map<String, Integer> order, String usernameCustomer) {
        synchronized (Order.class) {
            this.id = ++idCounter;
        }
        this.orderDate = LocalDateTime.now();
        this.statusOrder = StatusOrder.PROCESSING;
        this.totalPrice = calculateTotalPrice(order);
        this.usernameCustomer = usernameCustomer;
        this.order = order;
    }

    public Order(String usernameCustomer) {
        synchronized (Order.class) {
            this.id = ++idCounter;
        }
        this.orderDate = LocalDateTime.now();
        this.statusOrder = StatusOrder.PROCESSING;
        this.totalPrice = 0;
        this.usernameCustomer = usernameCustomer;
        order = new HashMap<>();
    }

    public void addProduct(String productKey, int quantity) throws InactiveProductException,
        ErrorInProductNameException {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }

        if (productKey == null || productKey.isBlank()) {
            throw new IllegalArgumentException("You should enter product key");
        }

        if (!productRepository.isProductActive(productKey) && productRepository.isProductInactive(productKey)) {
            LOGGER.warn("User is interested in ordering this inactive product: {} ", productKey);
            throw new InactiveProductException("This product is inactive, can't be added to order!");
        }

        if (!productRepository.isProductActive(productKey) && !productRepository.isProductInactive(productKey)) {
            throw new ErrorInProductNameException("This product doesn't exist! Check for typo mistakes.");
        }

        warehouse.reduceStock(productKey, quantity);
        order.put(productKey, order.getOrDefault(productKey, 0) + quantity);

        Product product = productRepository.getProduct(productKey);
        totalPrice += (product.calculatePrice() * quantity);
        LOGGER.info("Product {} added {} times successfully!", productKey, quantity);

    }

    @SuppressWarnings("checkstyle:MethodLength")
    public void removeProduct(String productKey, Integer quantity) throws ProductNotInOrderException {
        if (productKey == null || productKey.isBlank()) {
            throw new IllegalArgumentException("Product key cannot be empty string.");
        }

        if (!order.containsKey(productKey)) {
            throw new ProductNotInOrderException("The product is not in the order and cannot be removed.");
        }

        if (quantity < 0) {
            throw new IllegalArgumentException("The quantity you want to remove cannot be less than 0");
        }

        int currentQuantity = getQuantityProduct(productKey);

        if (quantity > currentQuantity) {
            throw new IllegalArgumentException(
                "The quantity you want to remove has to be less or equal to current quantity!");
        }

        if (currentQuantity == quantity) {
            order.remove(productKey);
        } else {
            order.put(productKey, currentQuantity - quantity);
        }

        Product product = productRepository.getProduct(productKey);
        totalPrice -= (product.calculatePrice() * order.getOrDefault(productKey, 1));
        LOGGER.info("Product {} removed {} successfully!", productKey, quantity);

    }

    private double calculateTotalPrice(Map<String, Integer> order) {
        double total = 0.0;
        for (Map.Entry<String, Integer> entry : order.entrySet()) {
            total += productRepository.getActiveProduct(entry.getKey()).calculatePrice() * entry.getValue();
        }
        return total;
    }

    public Map<String, Integer> getOrder() {
        return Collections.unmodifiableMap(order);
    }

    public StatusOrder getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(StatusOrder statusOrder) {
        this.statusOrder = statusOrder;
    }

    public void setUsernameCustomer(String usernameCustomer) {
        this.usernameCustomer = usernameCustomer;
    }

    public void resetOrderDate() {
        this.orderDate = LocalDateTime.now();
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public Integer getQuantityProduct(String productKey) {
        return order.getOrDefault(productKey, 0);
    }

    public double getTotalPrice() {
        return BigDecimal.valueOf(totalPrice)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
    }

    public String getUsernameCustomer() {
        return usernameCustomer;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Order order = (Order) object;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        StringBuilder orderItems = new StringBuilder();
        for (Map.Entry<String, Integer> entry : order.entrySet()) {
            orderItems.append("\n\t").append("Product: ").append(entry.getKey()).append("; Quantity: ")
                .append(entry.getValue());
        }

        return "\nOrder : " + orderItems;
    }

    public int getOrderId() {
        return id;
    }

}
