package com.deliciouspizza.entity.order;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.InactiveProductException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.exception.ProductNotInOrderException;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.enums.StatusOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// StorageProduct
// class StorageProductRepository

// order --> checkProduct

// Singleton<ProductRepository>

//StorageProduct  =
//storagfe.checkProductsAvailability....
//checkProductsAvailability(order)

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    private static int idCounter = 0; // Статичен брояч за уникални ID
    private final int id;

    private final Map<String, Integer> order;
    private StatusOrder statusOrder;
    private final LocalDateTime orderDate;
    private double totalPrice;
    private String usernameCustomer;

    private static final ProductRepository PRODUCT_REPOSITORY = Singleton.getInstance(ProductRepository.class);

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

    public void addProduct(String productKey, int quantity) throws InactiveProductException {
        if (productKey.isEmpty()) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }

        if (!PRODUCT_REPOSITORY.isProductActive(productKey)) {
            throw new InactiveProductException("This product is inactive, can't be added to order!");
        }

        order.put(productKey, order.getOrDefault(productKey, 0) + quantity);

        try {
            Product product = PRODUCT_REPOSITORY.getProduct(productKey);
            totalPrice += (product.calculatePrice() * quantity);
        } catch (ProductDoesNotExistException err) {
            System.out.println(err.getMessage());
        }

    }

    public void removeProduct(String productKey, Integer quantity) throws ProductNotInOrderException {
        if (productKey.isEmpty()) {
            throw new IllegalArgumentException("Product key cannot be null.");
        }

        if (!order.containsKey(productKey)) {
            throw new ProductNotInOrderException("The product is not in the order and cannot be removed.");
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

        try {
            Product product = PRODUCT_REPOSITORY.getProduct(productKey);
            totalPrice -= (product.calculatePrice() * order.getOrDefault(productKey, 1));
            System.out.println("Product removed successfully!");

        } catch (ProductDoesNotExistException err) {
            System.out.println(err.getMessage());
        }
    }

    private double calculateTotalPrice(Map<String, Integer> order) {
        double total = 0.0;
        for (Map.Entry<String, Integer> entry : order.entrySet()) {
            total += PRODUCT_REPOSITORY.getActiveProduct(entry.getKey()).calculatePrice() * entry.getValue();
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
