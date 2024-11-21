package com.deliciouspizza.repository;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.utils.Singleton;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Warehouse {
    private static final ProductRepository PRODUCT_REPOSITORY = Singleton.getInstance(ProductRepository.class);
    private final Map<String, Integer> productStock;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";

    private static final String FILE_PATH_STOCK = "src/main/resources/stock.json";
    private final File jsonFileStock = new File(FILE_PATH_STOCK);


    private final ObjectMapper objectMapper;

    public Warehouse() {
        this.productStock = new ConcurrentHashMap<>();
        objectMapper = new ObjectMapper();

        loadStock();
    }

    public void loadStock() {
        try {
            if (!jsonFileStock.exists() || jsonFileStock.length() == 0) {
                productStock.clear();
            } else {
                ObjectMapper mapper = new ObjectMapper();
                productStock.putAll(mapper.readValue(jsonFileStock, new TypeReference<Map<String, Integer>>() {
                }));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading stock data.");
        }
    }

    public void saveStock() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFileStock, productStock);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving stock data.");
        }
    }

    public synchronized void addStock(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product can't be null");
        }

        String productKey = product.generateKey();

        productStock.put(productKey, productStock.getOrDefault(productKey, 0) + quantity);
        saveStock();
    }

    public synchronized void reduceStock(String productName, int quantity) {
        if (!productStock.containsKey(productName) || productStock.get(productName) < quantity) {
            throw new IllegalArgumentException("Not enough stock of product: " + productName);
        }

        int remainingStock = productStock.get(productName) - quantity;

        if (remainingStock == 0) {
            productStock.remove(productName);
        } else {
            productStock.put(productName, remainingStock);
        }

        saveStock();
    }

    public synchronized void reduceStockWithOrder(Order order) {
        for (Map.Entry<String, Integer> entry : order.getOrder().entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();

            if (!productStock.containsKey(productName) || productStock.get(productName) < quantity) {
                throw new IllegalArgumentException("Not enough stock of product: " + productName);
            }

            int remainingStock = productStock.get(productName) - quantity;

            if (remainingStock == 0) {
                productStock.remove(productName);
            } else {
                productStock.put(productName, remainingStock);
            }
        }

        saveStock();
    }

    public synchronized boolean hasEnoughStock(String productName, int requestedQuantity) {
        return productStock.getOrDefault(productName, 0) >= requestedQuantity;
    }

    public boolean doesProductExist(String productKey) {
        return productStock.containsKey(productKey);
    }

    public void printStock() {
        System.out.println("Product Stock List:");
        System.out.println(BLUE + "----------------------------" + RESET);
        for (Map.Entry<String, Integer> entry : productStock.entrySet()) {
            String product = entry.getKey();
            int stock = entry.getValue();

            product = capitalizeWords(product.replaceAll("_", " "));

            System.out.printf(BLUE + "- " + RESET + "%-30s %sStock: %-3d%s" + BLUE + "   KEY" + RESET + ":%s\n",
                product,
                GREEN, stock,
                RESET, entry.getKey());
        }
        System.out.println(BLUE + "----------------------------" + RESET);
    }

    private String capitalizeWords(String input) {
        String[] words = input.split("_");
        StringBuilder capitalizedString = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalizedString.append(word.substring(0, 1).toUpperCase());
                capitalizedString.append(word.substring(1).toLowerCase());
                capitalizedString.append(" ");
            }
        }
        return capitalizedString.toString().trim();
    }

//    public Product getProductByKey(String productKey) {
//        return productStock.get(productKey);
//    }

}
