package com.deliciouspizza.enums;

public enum SauceType {
    TOMATO_SAUCE(1.00),
    GARLIC_SAUCE(1.20),
    BBQ_SAUCE(1.50),
    SPICY_SAUCE(1.30),
    RANCH(1.40),
    PESTO(1.70),
    WHITE_SAUCE(1.50),
    HONEY_MUSTARD(1.30);

    private final double price;

    SauceType(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
