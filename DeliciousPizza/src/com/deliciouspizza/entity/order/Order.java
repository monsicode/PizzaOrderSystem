package com.deliciouspizza.entity.order;

import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.user.User;
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
    private User customer;
    private User employee;
    private String addressDelivery;

    // do i really need this constructor ? // thought that the price had a problem, -> no problem
    public Order() {
        this.id = UUID.randomUUID().toString();
        this.order = new HashMap<>();
        this.orderDate = LocalDateTime.now();
        this.totalPrice = 0.0;
    }

    public Order(Map<Product, Integer> order, StatusOrder statusOrder, User customer, User employee,
                 String addressDelivery) {
        this.id = UUID.randomUUID().toString();
        this.order = order;
        this.statusOrder = statusOrder;
        this.orderDate = LocalDateTime.now();
        this.totalPrice = calculateTotalPrice(order);
        this.customer = customer;
        this.employee = employee;
        this.addressDelivery = addressDelivery;
    }

    public void addProduct(Product product, int quantity) {
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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public User getCustomer() {
        return customer;
    }

    public User getEmployee() {
        return employee;
    }

    public String getAddressDelivery() {
        return addressDelivery;
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
}
