package com.deliciouspizza;

import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.product.Sauce;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.utils.DrinkType;
import com.deliciouspizza.utils.DrinkVolume;
import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.deliciouspizza.utils.SauceType;

public class Main {

    @SuppressWarnings("checkstyle:MethodLength")
    public static void main(String[] args) {

        ProductRepository repository = new ProductRepository();

        Product pizza1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
        Product pizza2 = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        Product pizza3 = new Pizza(PizzaType.PEPPERONI, PizzaSize.LARGE);
        Product drink = new Drink(DrinkType.COKE, DrinkVolume.GRANDE);
        Product drink2 = new Drink(DrinkType.BEER, DrinkVolume.GRANDE);

        repository.addProduct(pizza1);
        repository.addProduct(pizza2);
        repository.addProduct(pizza3);
        repository.addProduct(drink);
        repository.addProduct(drink2);

        //---------------------------------------------------

        ProductService service = new ProductService(repository);

        service.deactivateProduct(pizza1);
        System.out.println(service.getActiveProducts());

        Product sauce = new Sauce(SauceType.BBQ_SAUCE);

        service.addNewProduct(sauce);

        System.out.println(service.getProductPrice(pizza1));
        System.out.println(service.getProductPrice(drink));

        //----------------------------------------------------

    }

}
