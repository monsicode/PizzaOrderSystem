package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonPropertyOrder({"type", "pizzaType", "pizzaSize", "statusProduct"})
public class Pizza extends Food {
    //private static Map<id, pizza>
    private final PizzaType pizzaType;
    private final PizzaSize pizzaSize;

    public Pizza() {
        pizzaSize = null;
        pizzaType = null;
    }

    public Pizza(PizzaType pizzaType, PizzaSize pizzaSize) {
        this.pizzaType = pizzaType;
        this.pizzaSize = pizzaSize;
    }

    @Override
    public double calculatePrice() {
        return pizzaType.getPrice() + pizzaSize.getAdditionalPrice();
    }

    @Override
    public String generateKey() {
        key = "pizza_" + pizzaType.toString().toLowerCase();
        return key;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Pizza pizza = (Pizza) object;
        return pizzaType == pizza.pizzaType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pizzaType);
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
            "pizzaSize=" + pizzaSize +
            ",pizzaType=" + pizzaType +
            "}\n";
    }

}
