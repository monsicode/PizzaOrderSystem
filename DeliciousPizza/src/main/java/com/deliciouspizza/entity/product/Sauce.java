package com.deliciouspizza.entity.product;

import com.deliciouspizza.enums.SauceType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"type", "sauce", "statusProduct"})
public class Sauce extends Product {

    private final SauceType sauce;

    public Sauce() {
        this.sauce = null;
        this.type = "sauce";
    }

    public Sauce(SauceType sauce) {
        this.sauce = sauce;
        this.type = "sauce";
        key = generateKey();
    }

    @Override
    public double calculatePrice() {
        if (sauce == null) {
            throw new IllegalArgumentException("Sauce is null, can't calculate price!");
        }

        return sauce.getPrice();
    }

    @Override
    public String generateKey() {
        if (sauce == null) {
            throw new IllegalArgumentException("Sauce is null, can't generate key!");
        }

        return "sauce_" + sauce.toString().toLowerCase();
    }

    public SauceType getSauce() {
        return sauce;
    }

    @Override
    public String toString() {
        return "Sauce{" +
            "sauce=" + sauce +
            "}\n";
    }

    private String getNameSauce() {
        return String.valueOf(sauce).replaceAll("_", " ");
    }

    @Override
    public String getFormattedDetails() {
        return String.format("Sauce: %s", getNameSauce());
    }

}
