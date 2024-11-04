package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.StatusProduct;

public abstract class Food extends Product {

    public Food(String nameProduct, StatusProduct statusProduct) {
        super(nameProduct, statusProduct);
    }

}
