package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.DrinkVolume;
import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.deliciouspizza.utils.StatusProduct;

public abstract class Product {
    protected final String nameProduct;
    protected StatusProduct statusProduct;

    public Product(String nameProduct, StatusProduct statusProduct) {
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

    public static void main(String[] args) {
        Product p1 = new Pizza("bab", StatusProduct.ACTIVE, PizzaType.MARGHERITA, PizzaSize.LARGE);
        Product p2 = new Drink("cola", StatusProduct.ACTIVE, true, DrinkVolume.GRANDE);
        System.out.println(p2.calculatePrice());
    }

}
