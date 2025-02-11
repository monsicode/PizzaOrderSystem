package com.deliciouspizza.enums;

public enum PizzaType {
    MARGHERITA(8.00),
    PEPPERONI(9.00),
    HAWAIIAN(10.00),
    VEGGIE(9.50),
    BBQ_CHICKEN(11.00),
    FOUR_CHEESE(10.50),
    SEAFOOD(12.00),
    MEAT_LOVERS(11.50);

    private final double price;

    PizzaType(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public static boolean isValid(String type) {
        try {
            PizzaType.valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
