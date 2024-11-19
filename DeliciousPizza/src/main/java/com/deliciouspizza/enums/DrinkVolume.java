package com.deliciouspizza.enums;

public enum DrinkVolume {
    SHORT(3),
    TALL(3.50),
    GRANDE(4),
    VENTI(4.50);

    private final double price;

    DrinkVolume(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
