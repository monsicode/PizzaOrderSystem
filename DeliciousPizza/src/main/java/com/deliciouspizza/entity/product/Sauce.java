package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.SauceType;

public class Sauce extends Product {

    private final SauceType sauce;

    public Sauce(SauceType sauce) {
        this.sauce = sauce;
    }

    @Override
    public double calculatePrice() {
        return sauce.getPrice();
    }
}
