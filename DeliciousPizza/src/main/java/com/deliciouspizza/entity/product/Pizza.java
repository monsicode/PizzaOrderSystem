package com.deliciouspizza.entity.product;

import com.deliciouspizza.enums.PizzaSize;
import com.deliciouspizza.enums.PizzaType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

//exceptions --> thrown

@JsonPropertyOrder({"type", "pizzaType", "pizzaSize", "statusProduct"})
public class Pizza extends Food {
    private final PizzaType pizzaType;
    private final PizzaSize pizzaSize;

    public Pizza() {
        pizzaSize = null;
        pizzaType = null;
        this.type = "pizza";
    }

    public Pizza(PizzaType pizzaType, PizzaSize pizzaSize) {
        this.pizzaType = pizzaType;
        this.pizzaSize = pizzaSize;
        this.type = "pizza";
    }

    @Override
    public double calculatePrice() {
        if (pizzaType == null) {
            throw new IllegalArgumentException("Pizza type is null, can't calculate price!");
        }
        if (pizzaSize == null) {
            throw new IllegalArgumentException("Pizza type is null, can't calculate price!");
        }

        return pizzaType.getPrice() + pizzaSize.getAdditionalPrice();
    }

    @Override
    public String generateKey() {
        if (pizzaType == null) {
            throw new IllegalArgumentException("Pizza type is null, can't generate key!");
        }

        if (pizzaSize == null) {
            throw new IllegalArgumentException("Pizza type is null, can't generate key!");
        }

        key = "pizza_" + pizzaType.toString().toLowerCase() + "_" + pizzaSize.toString().toLowerCase();
        return key;
    }

    public PizzaSize getPizzaSize() {
        return pizzaSize;
    }

    public PizzaType getPizzaType() {
        return pizzaType;
    }

    @Override
    public String toString() {
        return "Pizza{" +
            "pizzaType=" + pizzaType +
            ", pizzaSize=" + pizzaSize +
            ", type='" + type + '\'' +
            "}\n";
    }

    @Override
    public String getFormattedDetails() {
        return String.format("Pizza %s: Size = %s", pizzaType, pizzaSize);
    }
}
