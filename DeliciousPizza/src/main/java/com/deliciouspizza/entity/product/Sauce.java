package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.SauceList;

public class Sauce extends Product {

    private final SauceList sauce;

    public Sauce(SauceList sauce) {
        this.sauce = sauce;
    }

    @Override
    public double calculatePrice() {
        return sauce.getPrice();
    }
}
