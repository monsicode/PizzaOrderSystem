package com.deliciouspizza.utils;

public enum DrinkVolume {
    SHORT(3),
    TALL(3.50),
    GRANDE(4),
    VENTI(4.50),
    TRENTA(5.20);

    private final double price;

    DrinkVolume(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
