package com.deliciouspizza.entity.product;

import com.deliciouspizza.enums.StatusProduct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

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

    @JsonProperty("type")
    protected String type;

    public Product() {
        this.statusProduct = StatusProduct.INACTIVE;
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

    @JsonIgnore
    public abstract String getFormattedDetails();

    @JsonIgnore
    public boolean isItGoodForUnderAgedCustomers() {
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Product product = (Product) object;
        return Objects.equals(key, product.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}
