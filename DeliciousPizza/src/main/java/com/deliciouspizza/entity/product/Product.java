package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.StatusProduct;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

//do i need this ?
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
//@JsonSubTypes({
//    @JsonSubTypes.Type(value = Pizza.class, name = "pizza"),
//    @JsonSubTypes.Type(value = Drink.class, name = "drink"),
//    @JsonSubTypes.Type(value = Sauce.class, name = "sauce")
//})

public abstract class Product {

    protected StatusProduct statusProduct;
    protected String key;

    public Product() {
        this.statusProduct = StatusProduct.ACTIVE;
    }

    public void activateProduct() {
        statusProduct = StatusProduct.ACTIVE;
    }

    public void deactivateProduct() {
        statusProduct = StatusProduct.INACTIVE;
    }

    public StatusProduct getStatusProduct() {
        return statusProduct;
    }

    public abstract double calculatePrice();

    public abstract String generateKey();

}
