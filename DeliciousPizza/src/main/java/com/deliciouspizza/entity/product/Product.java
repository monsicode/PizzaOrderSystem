package com.deliciouspizza.entity.product;

import com.deliciouspizza.exception.ProductAlreadyActiveException;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.utils.StatusProduct;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

//exceptions -> thrown

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Pizza.class, name = "pizza"),
    @JsonSubTypes.Type(value = Drink.class, name = "drink"),
    @JsonSubTypes.Type(value = Sauce.class, name = "sauce")
})

public abstract class Product {

    protected StatusProduct statusProduct;
    protected String key;

    // To ensure that it will be written to JSON
    @JsonProperty("type")
    protected String type;

    public Product() {
        this.statusProduct = StatusProduct.ACTIVE;
    }

    public void activateProduct() throws ProductAlreadyActiveException {
        if (statusProduct == StatusProduct.INACTIVE) {
            throw new ProductAlreadyActiveException("The product is already active!");
        }
        statusProduct = StatusProduct.ACTIVE;
    }

    public void deactivateProduct() throws ProductAlreadyDeactivatedException {
        if (statusProduct == StatusProduct.INACTIVE) {
            throw new ProductAlreadyDeactivatedException("The product is already deactivated!");
        }

        statusProduct = StatusProduct.INACTIVE;
    }

    public StatusProduct getStatusProduct() {
        return statusProduct;
    }

    public abstract double calculatePrice();

    public abstract String generateKey();

}
