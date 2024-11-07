package com.deliciouspizza.entity.product;

import com.deliciouspizza.utils.StatusProduct;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Food.class, name = "food"),
    @JsonSubTypes.Type(value = Pizza.class, name = "pizza"),
    @JsonSubTypes.Type(value = Drink.class, name = "drink"),
    @JsonSubTypes.Type(value = Sauce.class, name = "sauce")
})

public abstract class Product {

    protected StatusProduct statusProduct;

    public Product() {
        this.statusProduct = StatusProduct.ACTIVE;
    }

//    @Override
//    public String toString() {
//        return "Product{" +
//            " status = " + statusProduct + '\'';
//    }

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

//    @SuppressWarnings("checkstyle:MagicNumber")
//    public static void main(String[] args) {
////       Product p1 = new Pizza("bab", StatusProduct.ACTIVE, PizzaType.MARGHERITA, PizzaSize.LARGE);
////       Product p2 = new Drink("cola", StatusProduct.ACTIVE, true, DrinkVolume.GRANDE);
//
//        //  equal(), hash() -> във всяко едно листо
//        Product p1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
//        Product p2 = new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL);
//
//        Order o = new Order();
//        o.addProduct(p1, 1);
//        o.addProduct(p2, 6);
//
//        System.out.println(o);
//
////        System.out.println(p1.equals(p2));
////        p1.deactivateProduct();
////
////        System.out.println(p1.getStatusProduct());
////        System.out.println(p2.getStatusProduct());
//
//    }

}
