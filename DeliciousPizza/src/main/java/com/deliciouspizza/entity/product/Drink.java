package com.deliciouspizza.entity.product;

import com.deliciouspizza.enums.DrinkType;
import com.deliciouspizza.enums.DrinkVolume;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"type", "drink", "volume", "statusProduct"})
public class Drink extends Product {

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
        key = generateKey();
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

        if (volume == null) {
            throw new IllegalArgumentException("Drink volume is null, can't generate key!");
        }

        return "drink_" + drink.toString().toLowerCase() + "_" + volume.toString().toLowerCase();
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
    public String getFormattedDetails() {
        return String.format("Drink: %s, Volume: %s", drink, volume);
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
    public boolean isItGoodForUnderAgedCustomers() {
        return !getIsAlcoholic();
    }

}
