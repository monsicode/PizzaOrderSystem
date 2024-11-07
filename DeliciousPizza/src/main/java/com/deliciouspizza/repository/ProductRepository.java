package com.deliciouspizza.repository;

import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.utils.PizzaSize;
import com.deliciouspizza.utils.PizzaType;
import com.deliciouspizza.utils.StatusProduct;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductRepository {

    private static final String FILE_PATH = "src/main/resources/product.json";
    private Map<Product, StatusProduct> products;
    private ObjectMapper objectMapper;

    public ProductRepository() {
        products = new HashMap<>();
        objectMapper = new ObjectMapper();
        loadProducts();
    }

    public void addProduct(Product product, StatusProduct status) {
        products.put(product, status);
        saveProducts();
    }

    public void loadProducts() {
        try {
            products = objectMapper.readValue(new File(FILE_PATH),
                objectMapper.getTypeFactory().constructMapType(Map.class, Product.class, StatusProduct.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveProducts() {
        try {
            File file = new File(FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
            }
            objectMapper.writeValue(file, products);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        ProductRepository productRepository = new ProductRepository();
//
//        // Пример за създаване на нов продукт (например пица)
//        Product pizza = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
//
//        // Добавяне на продукта в репозиторито със статус ACTIVE
//        productRepository.addProduct(pizza, StatusProduct.ACTIVE);
//
//        // Извеждаме всички активни продукти
//        System.out.println("All active products:");
//        for (Product product : productRepository.products.keySet()) {
//            System.out.println(product);
//        }
//
//        // Пример за сериализация и десериализация на данни
//        productRepository.saveProducts();  // Записване на активните продукти в JSON файл
//        productRepository.loadProducts();  // Зареждане на продукти от JSON файл
//    }

}
