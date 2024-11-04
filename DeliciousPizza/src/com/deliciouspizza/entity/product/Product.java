package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.DrinkVolume;
import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.deliciouspizza.utils.StatusProduct;

import java.util.Objects;
import java.util.UUID;

public abstract class Product {
    private final String id;
    protected final String nameProduct;
    protected StatusProduct statusProduct;

    public Product(String nameProduct, StatusProduct statusProduct) {
        this.id = UUID.randomUUID().toString();
        this.nameProduct = nameProduct;
        this.statusProduct = statusProduct;
    }

    public void activateProduct() {
        statusProduct = StatusProduct.ACTIVE;
    }

    public void deactivateProduct() {
        statusProduct = StatusProduct.INACTIVE;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public StatusProduct getStatusProduct() {
        return statusProduct;
    }

    public abstract double calculatePrice();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Product product = (Product) object;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static void main(String[] args) {
        Product p1 = new Pizza("bab", StatusProduct.ACTIVE, PizzaType.MARGHERITA, PizzaSize.LARGE);
        Product p2 = new Drink("cola", StatusProduct.ACTIVE, true, DrinkVolume.GRANDE);
        System.out.println(p2.calculatePrice());
    }

}
