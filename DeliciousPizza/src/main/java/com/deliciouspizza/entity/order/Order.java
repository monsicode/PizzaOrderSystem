package com.deliciouspizza.entity.order;

import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.utils.StatusOrder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private final String id;

    private final Map<Product, Integer> order;
    private StatusOrder statusOrder;
    private final LocalDateTime orderDate;
    private double totalPrice;
    private final String usernameCustomer;
    private String addressDelivery;

    //checkProductsAvailability(order)

    public Order(Map<Product, Integer> order, String usernameCustomer) {
        this.id = UUID.randomUUID().toString();
        this.order = order;
        this.orderDate = LocalDateTime.now();
        this.statusOrder = StatusOrder.PROCESSING;
        this.totalPrice = calculateTotalPrice(order);
        this.usernameCustomer = usernameCustomer;
        this.addressDelivery = null;
    }

    public void addProduct(Product product, int quantity) {
        //if product is active
        order.put(product, order.getOrDefault(product, 0) + quantity);
        totalPrice += product.calculatePrice() * quantity;
    }

    public void removeProduct(Product product) {
        if (order.containsKey(product)) {
            totalPrice -= product.calculatePrice() * order.get(product);
            order.remove(product);
        }
    }

    private double calculateTotalPrice(Map<Product, Integer> order) {
        double total = 0.0;
        for (Map.Entry<Product, Integer> entry : order.entrySet()) {
            total += entry.getKey().calculatePrice() * entry.getValue();
        }
        return total;
    }

    public Map<Product, Integer> getOrder() {
        return Collections.unmodifiableMap(order);
    }

    public StatusOrder getStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(StatusOrder statusOrder) {
        this.statusOrder = statusOrder;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getAddressDelivery() {
        return addressDelivery;
    }

    public void setAddressDelivery(String addressDelivery) {
        this.addressDelivery = addressDelivery;
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
        for (Map.Entry<Product, Integer> entry : order.entrySet()) {
            orderItems.append("\n\t").append(entry.getKey().toString()).append(", Quantity: ").append(entry.getValue());
        }

        return "Order" + "order=" + orderItems;
    }

}
