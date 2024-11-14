package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.DrinkType;
import com.deliciouspizza.utils.DrinkVolume;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;

//exceptions --> thrown

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
        if (drink == null) {
            throw new IllegalArgumentException("Drink type is null, can't calculate price!");
        }
        if (volume == null) {
            throw new IllegalArgumentException("Drink volume is null, can't calculate price!");
        }

        return drink.getPrice() + volume.getPrice();
    }

    @Override
    public String generateKey() {
        if (drink == null) {
            throw new IllegalArgumentException("Drink type is null, can't generate key!");
        }

        key = "drink_" + drink.toString().toLowerCase();
        return key;
    }

    public boolean getIsAlcoholic() {
        if (drink == null) {
            throw new IllegalArgumentException("Drink type is null, can't get if it's alcoholic!");
        }

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
            ", volume=" + volume +
            ", type='" + type + '\'' +
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
