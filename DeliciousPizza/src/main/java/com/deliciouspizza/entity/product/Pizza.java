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
        this.type = "pizza";
    }

    public Pizza(PizzaType pizzaType, PizzaSize pizzaSize) {
        this.pizzaType = pizzaType;
        this.pizzaSize = pizzaSize;
        this.type = "pizza";
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
            '}';
    }
}
