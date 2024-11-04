package com.deliciouspizza.entity.product;

import com.deliciouspizza.exception.runtime.InvalildPriceException;
import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.deliciouspizza.utils.StatusProduct;

public abstract class Product {
    private final String nameProduct;
    private double price;
    private StatusProduct statusProduct;

    public Product(String nameProduct, double price, StatusProduct statusProduct) {
        this.nameProduct = nameProduct;
        setPrice(price);
        this.statusProduct = statusProduct;
    }

    private void setPrice(double price) {
        if (price < 0) {
            throw new InvalildPriceException("The price you tried to set is invalid!");
        }
        this.price = price;
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
    public double getPrice() {
        return price;
    }
    public StatusProduct getStatusProduct() {
        return statusProduct;
    }


    public static void main(String[] args) {
        Pizza p1 = new Pizza("Pizza margarita",14.99,StatusProduct.ACTIVE, PizzaType.MARGHERITA, PizzaSize.MEDIUM);
        System.out.println(p1.getPizzaType());
    }

}
