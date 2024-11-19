package com.deliciouspizza.enums;

public enum PizzaSize {
    SMALL(0),
    MEDIUM(3.00),
    LARGE(4.50);

    double additionalPrice;

    PizzaSize(double additionalPrice) {
        this.additionalPrice = additionalPrice;
    }

    public double getAdditionalPrice() {
        return additionalPrice;
    }

}
