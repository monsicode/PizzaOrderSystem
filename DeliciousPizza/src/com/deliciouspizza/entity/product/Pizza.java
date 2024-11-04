package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.deliciouspizza.utils.StatusProduct;

public class Pizza extends Food{

    private final PizzaType pizzaType;
    private final PizzaSize pizzaSize;

    public Pizza(String nameProduct, double price, StatusProduct statusProduct, PizzaType pizzaType, PizzaSize pizzaSize) {
        super(nameProduct, price, statusProduct);
        this.pizzaType = pizzaType;
        this.pizzaSize = pizzaSize;
    }

    public PizzaType getPizzaType() {
        return pizzaType;
    }

    public PizzaSize getPizzaSize() {
        return pizzaSize;
    }
}
