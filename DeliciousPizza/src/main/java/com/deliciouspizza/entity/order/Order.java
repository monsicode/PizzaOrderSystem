package com.deliciouspizza.entity.order;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.InactiveProductException;
import com.deliciouspizza.exception.ProductNotInOrderException;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.utils.StatusOrder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

// StorageProduct
// class StorageProductRepository

// order --> checkProduct

// Singleton<ProductRepository>

//StorageProduct  =
//storagfe.checkProductsAvailability....
//checkProductsAvailability(order)

public class Order {

    private final String id;

    private final Map<String, Integer> order;
    private StatusOrder statusOrder;
    private final LocalDateTime orderDate;
    private double totalPrice;
    private String usernameCustomer;

    private final ProductRepository productRepository = Singleton.getInstance(ProductRepository.class);

    public Order() {
        this.id = UUID.randomUUID().toString();
        this.orderDate = LocalDateTime.now();
        this.statusOrder = StatusOrder.PROCESSING;
        order = new HashMap<>();
    }

    public Order(Map<String, Integer> order, String usernameCustomer) {
        this.id = UUID.randomUUID().toString();
        this.orderDate = LocalDateTime.now();
        this.statusOrder = StatusOrder.PROCESSING;
        this.totalPrice = calculateTotalPrice(order);
        this.usernameCustomer = usernameCustomer;
        this.order = order;
    }

    public Order(String usernameCustomer) {
        this.id = UUID.randomUUID().toString();
        this.orderDate = LocalDateTime.now();
        this.statusOrder = StatusOrder.PROCESSING;
        this.totalPrice = 0;
        this.usernameCustomer = usernameCustomer;
        order = new HashMap<>();
    }

    //handle where?
    //String key
    public void addProduct(Product product, int quantity) throws InactiveProductException {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }

        if (!productRepository.isProductActive(product)) {
            throw new InactiveProductException("This product is inactive, can't be added to order!");
        }

        order.put(product.generateKey(), order.getOrDefault(product.generateKey(), 0) + quantity);
        totalPrice += (product.calculatePrice() * quantity);
        System.out.println("Product added successfully!");
    }

    public void removeProduct(Product product) throws ProductNotInOrderException {
        if (!order.containsKey(product.generateKey())) {
            throw new ProductNotInOrderException("The product is not in the order and cannot be removed.");
        }

        totalPrice -= (product.calculatePrice() * order.get(product.generateKey()));
        order.remove(product.generateKey());
        System.out.println("Product removed successfully!");
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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
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
    //getOrderById()

}
