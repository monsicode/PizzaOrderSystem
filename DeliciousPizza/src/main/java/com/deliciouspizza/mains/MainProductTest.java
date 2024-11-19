package com.deliciouspizza.mains;

import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.product.Sauce;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.enums.DrinkType;
import com.deliciouspizza.enums.DrinkVolume;
import com.deliciouspizza.enums.PizzaSize;
import com.deliciouspizza.enums.PizzaType;
import com.deliciouspizza.enums.SauceType;

public class MainProductTest {

    public static void main(String[] args) {
        ProductService service = new ProductService();

        Product product1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.MEDIUM);
        Product product11 = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
        Product product2 = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);
        Product product3 = new Sauce(SauceType.GARLIC_SAUCE);

        Product product4 = new Pizza(PizzaType.PEPPERONI, PizzaSize.MEDIUM);
        Product product5 = new Drink(DrinkType.BEER, DrinkVolume.GRANDE);

        service.addNewProduct(product4);
        service.addNewProduct(product5);
      //  service.deactivateProduct(product1);
//        service.activateProduct(product2);

//        System.out.println(service.getAllActiveProducts());
//        System.out.println(service.getProductPrice(product2));
//
//        Product p = service.getProduct("pizza_margherita");
//        System.out.println(p);
    }

}
