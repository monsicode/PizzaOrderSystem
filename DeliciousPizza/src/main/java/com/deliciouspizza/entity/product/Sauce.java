package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.SauceType;

public class Sauce extends Product {

    private final SauceType sauce;

    public Sauce() {
        this.sauce = null;
        this.type = "sauce";
    }

    public Sauce(SauceType sauce) {
        this.sauce = sauce;
        this.type = "sauce";
    }

    @Override
    public double calculatePrice() {
        return sauce.getPrice();
    }

    @Override
    public String generateKey() {
        key = "sauce_" + sauce.toString().toLowerCase();
        return key;
    }
}
