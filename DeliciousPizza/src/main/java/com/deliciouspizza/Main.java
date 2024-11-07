package com.deliciouspizza;

import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.utils.DrinkType;
import com.deliciouspizza.utils.DrinkVolume;
import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Main {

    private static final String FILE_PATH = "src/main/resources/product.json";
    public static void main(String[] args) {

        ProductRepository repository = new ProductRepository();

        // Добавяне на продукти
        Product pizza1 = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
        Product pizza2 = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        Product pizza3 = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        repository.addProduct(pizza1);
        repository.addProduct(pizza2);
        repository.addProduct(pizza3);

        // Записване на продуктите в JSON файл
        repository.saveProducts();

        // Зареждане на продуктите от JSON файл
        repository.loadProducts();

        // Извеждаме продуктите
        System.out.println(repository.getProductsMap());
    }


}
