package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.DrinkList;
import com.deliciouspizza.utils.DrinkVolume;

import java.util.Objects;

public class Drink extends Product {

    // later to add in User to have age. if user is under 18 -> exception
    private final DrinkList drink;
    private final DrinkVolume volume;

    public Drink(DrinkList drink, DrinkVolume volume) {
        this.drink = drink;
        this.volume = volume;
    }

    @Override
    public double calculatePrice() {
        return drink.getPrice() + volume.getPrice();
    }

    public boolean getIsAlcoholic() {
        return drink.getisAlcoholic();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Drink drink1 = (Drink) object;
        return drink == drink1.drink;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(drink);
    }
}
