package com.deliciouspizza.repository;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Warehouse {

    private static final Logger LOGGER = LogManager.getLogger(Warehouse.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Map<String, Integer> productStock;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";

    private static final String FILE_PATH_STOCK = "src/main/resources/stock.json";
    private final File jsonFileStock = new File(FILE_PATH_STOCK);

    public Warehouse() {
        this.productStock = new ConcurrentHashMap<>();
        loadStock();
    }

    public synchronized void loadStock() {
        try {
            if (!jsonFileStock.exists() || jsonFileStock.length() == 0) {
                productStock.clear();
            } else {
                productStock.putAll(MAPPER.readValue(jsonFileStock, new TypeReference<Map<String, Integer>>() {
                }));
                LOGGER.info("Stock loaded successfully from file.");
            }
        } catch (IOException e) {
            LOGGER.error("Error loading stock data: {}", e.getMessage(), e);
        }
    }

    public synchronized void saveStock() {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(jsonFileStock, productStock);
            LOGGER.info("Stock saved successfully to file.");
        } catch (IOException e) {
            LOGGER.error("Error saving stock data: {}", e.getMessage(), e);
        }
    }

    public void addStock(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product can't be null");
        }

        String productKey = product.generateKey();
        productStock.put(productKey, productStock.getOrDefault(productKey, 0) + quantity);
        LOGGER.info("Added {} units of product {} to stock.", quantity, productKey);
        saveStock();
    }

    public void reduceStock(String productName, int quantity) {
        if (!productStock.containsKey(productName) || productStock.get(productName) < quantity) {
            throw new IllegalArgumentException("Not enough stock of product: " + productName);
        }

        int remainingStock = productStock.get(productName) - quantity;

        if (remainingStock == 0) {
            productStock.remove(productName);
        } else {
            productStock.put(productName, remainingStock);
        }

        LOGGER.info("Reduced stock of product {} by {} units. Remaining stock: {}", productName, quantity,
            remainingStock);
        saveStock();
    }

    public void reduceStockWithOrder(Order order) {
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

}
