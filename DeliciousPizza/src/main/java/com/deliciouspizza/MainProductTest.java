package com.deliciouspizza;

import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.product.Sauce;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.utils.DrinkType;
import com.deliciouspizza.utils.DrinkVolume;
import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.deliciouspizza.utils.SauceType;

public class MainProductTest {

    public static void main(String[] args) {
        ProductService service = new ProductService();

        Product product1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.MEDIUM);
        Product product11 = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
        Product product2 = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);
        Product product3 = new Sauce(SauceType.GARLIC_SAUCE);

//        service.addNewProduct(product3);
//
//        service.deactivateProduct(product2);
//        service.activateProduct(product2);

//        System.out.println(service.getAllActiveProducts());
        System.out.println(service.getProductPrice(product2));

        Product p = service.getProduct("pizza_margherita");
        System.out.println(p);
    }

}
