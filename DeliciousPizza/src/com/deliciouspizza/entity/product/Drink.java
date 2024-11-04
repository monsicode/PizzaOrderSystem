package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.DrinkVolume;
import com.deliciouspizza.utils.StatusProduct;

public class Drink extends Product {

    // later to add in User to have age. if user is under 18 -> exception
    private final boolean isAlcoholic;
    private final DrinkVolume volume;

    public Drink(String nameProduct, StatusProduct statusProduct, boolean isAlcoholic, DrinkVolume volume) {
        super(nameProduct, statusProduct);
        this.isAlcoholic = isAlcoholic;
        this.volume = volume;
    }

    @Override
    public double calculatePrice() {
        return volume.getPrice();
    }

    public boolean getIsAlcoholic() {
        return isAlcoholic;
    }

}
