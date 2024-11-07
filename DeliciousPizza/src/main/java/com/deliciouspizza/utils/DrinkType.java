package com.deliciouspizza.utils;

public enum DrinkType {
    COKE(false, 2.50),
    PEPSI(false, 2.40),
    WATER(false, 1.50),
    ORANGE_JUICE(false, 3.00),
    APPLE_JUICE(false, 3.00),
    FANTA(false, 2.50),
    SPRITE(false, 2.50),
    LEMONADE(false, 2.00),
    GREEN_TEA(false, 2.20),
    BEER(true, 3.00),
    WINE(true, 5.00);

    private final boolean isAlcoholic;
    private final double price;

    DrinkType(boolean isAlcoholic, double price) {
        this.isAlcoholic = isAlcoholic;
        this.price = price;
    }

    @Override
    public String toString() {
        return "DrinkList{" +
            "isAlcoholic=" + isAlcoholic +
            '}';
    }

    public boolean getisAlcoholic() {
        return isAlcoholic;
    }

    public double getPrice() {
        return price;
    }

}
