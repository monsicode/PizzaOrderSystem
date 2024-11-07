package com.deliciouspizza;

import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.product.Pizza;

import com.deliciouspizza.utils.DrinkList;
import com.deliciouspizza.utils.DrinkVolume;
import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Main {

    private static final String FILE_PATH = "src/main/resources/product.json";

    public static void main(String[] args) {

        ObjectMapper objectMapper = new ObjectMapper();

        Product product = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
        Product product2 = new Drink(DrinkList.COKE, DrinkVolume.GRANDE);

        try {

            objectMapper.writeValue(new File(FILE_PATH), new Product[] { product, product2 });
            System.out.println("Обектите са записани в product.json");

            // Зареждане на продуктите обратно от JSON файла
            Product[] loadedProducts = objectMapper.readValue(new File(FILE_PATH), Product[].class);
            for (Product p : loadedProducts) {
                System.out.println("Зареден обект: " + p);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
