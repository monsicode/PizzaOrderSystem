package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.StatusProduct;

public abstract class Food extends Product {

    public Food(String nameProduct, double price, StatusProduct statusProduct) {
        super(nameProduct, price, statusProduct);
    }

}
