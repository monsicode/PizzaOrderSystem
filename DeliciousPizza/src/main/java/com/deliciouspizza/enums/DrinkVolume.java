package com.deliciouspizza.enums;

public enum DrinkVolume {
    SMALL(1, 250),
    LARGE(3, 500);

    private final double price;
    private final int ml;

    DrinkVolume(double price, int ml) {
        this.price = price;
        this.ml = ml;
    }

    public double getPrice() {
        return price;
    }

    public int getMl() {
        return ml;
    }

}
