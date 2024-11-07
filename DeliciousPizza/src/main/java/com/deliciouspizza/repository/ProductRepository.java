package com.deliciouspizza.repository;

import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductRepository {
    private static final String FILE_PATH = "src/main/resources/product.json";
    private Map<String, Product> productsMap = new HashMap<>();
    private final ObjectMapper objectMapper;

    public ProductRepository() {
        productsMap = new HashMap<>();
        objectMapper = new ObjectMapper(); // Инициализираме objectMapper тук
    }

    // Метод за добавяне на продукт с уникален ключ
    public void addProduct(Product product) {
        String key = generateUniqueKey(product);

        // Добавяме продукта в Map-а само ако ключът е уникален
        if (key != null && !productsMap.containsKey(key)) {
            productsMap.put(key, product);
            saveProducts(); // Метод за запазване на промените в JSON
        } else {
            System.out.println("Продуктът вече съществува или ключът е невалиден.");
        }
    }


    // Генериране на уникален ключ на база тип продукт
    private String generateUniqueKey(Product product) {
        if (product instanceof Pizza) {
            Pizza pizza = (Pizza) product;
            return "pizza_" + pizza.getPizzaType().toString().toLowerCase();
        } else if (product instanceof Drink) {
            Drink drink = (Drink) product;
            return "drink_" + drink.getDrink().toString().toLowerCase();
        } else {
            return null; // Връщаме null, ако продуктът няма специфичен тип
        }
    }

    public Map<String, Product> getProductsMap() {
        return productsMap;
    }

    public void saveProducts() {
        try {
            // Записване на Map в JSON файл
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), productsMap);
            System.out.println("Продуктите са записани в файла.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Грешка при запис на продуктите.");
        }
    }

    public void loadProducts() {
        try {
            // Зареждаме Map от JSON файл
            productsMap = objectMapper.readValue(new File(FILE_PATH), Map.class);
            System.out.println("Продуктите са заредени от файла.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
