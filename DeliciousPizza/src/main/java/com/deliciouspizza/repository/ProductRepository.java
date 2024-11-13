package com.deliciouspizza.repository;

import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.utils.StatusProduct;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProductRepository {
    private static final String FILE_PATH_INACTIVE_PRODUCTS = "src/main/resources/inactiveProduct.json";
    private static final String FILE_PATH_ACTIVE_PRODUCTS = "src/main/resources/activeProducts.json";

    private Map<String, Product> inactiveProducts;
    private Map<String, Product> activeProducts;
    private final ObjectMapper objectMapper;

    public ProductRepository() {
        inactiveProducts = new HashMap<>();
        activeProducts = new HashMap<>();
        objectMapper = new ObjectMapper();

        loadActiveProducts();
        loadInActiveProducts();
    }

    // Метод за добавяне на продукт с уникален ключ
    public void addProduct(Product product) {
        String key = product.generateKey();

        // Добавяме продукта в Map-а само ако ключът е уникален
        if (key != null && !inactiveProducts.containsKey(key) && product.getStatusProduct() == StatusProduct.INACTIVE) {
            inactiveProducts.put(key, product);
            saveProducts(FILE_PATH_INACTIVE_PRODUCTS, inactiveProducts); // Метод за запазване на промените в JSON

        } else if (key != null && product.getStatusProduct() == StatusProduct.ACTIVE &&
            !activeProducts.containsKey(key)) {

            activeProducts.put(key, product);
            saveProducts(FILE_PATH_ACTIVE_PRODUCTS, activeProducts);

        } else {
            System.out.println("Продуктът вече съществува или ключът е невалиден.");
        }
    }

    public Map<String, Product> getActiveProductsMap() {
        return Collections.unmodifiableMap(activeProducts);
    }

    public Map<String, Product> getInActiveProductsMap() {
        return Collections.unmodifiableMap(inactiveProducts);
    }

    public void activateProduct(Product product) {
        String key = product.generateKey();
        //if checks
        inactiveProducts.remove(key);
        activeProducts.put(key, product);
        saveProducts(FILE_PATH_ACTIVE_PRODUCTS, activeProducts);
        saveProducts(FILE_PATH_INACTIVE_PRODUCTS, inactiveProducts);
    }

    public void deactivateProduct(Product product) {
        String key = product.generateKey();
        product.deactivateProduct();
        activeProducts.remove(key);
        inactiveProducts.put(key, product);
        saveProducts(FILE_PATH_ACTIVE_PRODUCTS, activeProducts);
        saveProducts(FILE_PATH_INACTIVE_PRODUCTS, inactiveProducts);
    }

    private void saveProducts(String path, Map<String, Product> productsMap) {
        try {
            // Записване на Map в JSON файл
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), productsMap);
            System.out.println("Продуктите са записани в файла.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Грешка при запис на продуктите.");
        }
    }

    public void loadActiveProducts() {
        try {
            // Зареждаме Map от JSON файл
//            activeProducts = objectMapper.readValue(new File(FILE_PATH_ACTIVE_PRODUCTS), Map.class);
            activeProducts = objectMapper.readValue(new File(FILE_PATH_ACTIVE_PRODUCTS),
                new TypeReference<Map<String, Product>>() {});
            System.out.println("Продуктите са заредени от файла.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadInActiveProducts() {
        try {
            // Зареждаме Map от JSON файл
//            inactiveProducts = objectMapper.readValue(new File(FILE_PATH_INACTIVE_PRODUCTS), Map.class);
            inactiveProducts = objectMapper.readValue(new File(FILE_PATH_INACTIVE_PRODUCTS),
                new TypeReference<Map<String, Product>>() {});
            System.out.println("Продуктите са заредени от файла.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Product getActiveProduct(String productName){
        return activeProducts.get(productName);
    }

    public Product getInactiveProduct(String productName){
        return inactiveProducts.get(productName);
    }



}
