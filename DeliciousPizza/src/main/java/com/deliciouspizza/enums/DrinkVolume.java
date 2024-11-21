package com.deliciouspizza.enums;

public enum DrinkVolume {
    SMALL(1, 250),
    MEDIUM(3, 500),
    LARGE(4, 1000);
//    SHORT(3),
//    TALL(3.50),
//    GRANDE(4),
//    VENTI(4.50);

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
