package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.DrinkType;
import com.deliciouspizza.utils.DrinkVolume;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"type", "drink", "volume", "statusProduct"})
public class Drink extends Product {

    // later to add in User to have age. if user is under 18 -> exception
    private final DrinkType drink;
    private final DrinkVolume volume;

    public Drink() {
        drink = null;
        volume = null;
        this.type = "drink";
    }

    public Drink(DrinkType drink, DrinkVolume volume) {
        this.drink = drink;
        this.volume = volume;
        this.type = "drink";
    }

    @Override
    public double calculatePrice() {
        //check if drink is null --> exception
        return drink.getPrice() + volume.getPrice();
    }

    @Override
    public String generateKey() {
        key = "drink_" + drink.toString().toLowerCase() + "_" + volume.toString().toLowerCase();
        return key;
    }

    public boolean getIsAlcoholic() {
        return drink.getisAlcoholic();
    }

    public DrinkType getDrink() {
        return drink;
    }

    public DrinkVolume getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "Drink{" +
            "drink=" + drink +
            ",volume=" + volume +
            "}\n";
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
