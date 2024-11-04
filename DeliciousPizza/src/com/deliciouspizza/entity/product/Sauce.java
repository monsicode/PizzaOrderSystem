package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.StatusProduct;

public class Sauce extends Product {

    private static final double PRICE_SAUCE = 0.5;

    public Sauce(String nameProduct, StatusProduct statusProduct) {
        super(nameProduct, statusProduct);
    }

    @Override
    public double calculatePrice() {
        return PRICE_SAUCE;
    }
}
