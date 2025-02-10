package com.deliciouspizza.repository;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.enums.DrinkType;
import com.deliciouspizza.enums.PizzaType;
import com.deliciouspizza.enums.SauceType;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.service.ProductService;
import com.deliciouspizza.utils.Singleton;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Warehouse {

    private static final Logger LOGGER = LogManager.getLogger(Warehouse.class);

    private final ObjectMapper objectMapper;
    private final Map<String, Integer> productStock;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[38;5;214m";

    private static final String FILE_PATH_STOCK = "data-storage/stock.json";
    private final File jsonFileStock;

    private final ProductService productService;

    public Warehouse(ObjectMapper objectMapper, ProductService productService, File jsonFileStock) {
        this.objectMapper = objectMapper;
        this.productService = productService;
        this.jsonFileStock = jsonFileStock;
        this.productStock = new ConcurrentHashMap<>();
    }

    public Warehouse() {
        productService = Singleton.getInstance(ProductService.class);
        this.objectMapper = new ObjectMapper();
        this.productStock = new ConcurrentHashMap<>();
        jsonFileStock = new File(FILE_PATH_STOCK);
        loadStock();
    }

    public synchronized void loadStock() {
        try {
            if (!jsonFileStock.exists() || jsonFileStock.length() == 0) {
                productStock.clear();
            } else {
                productStock.putAll(objectMapper.readValue(jsonFileStock, new TypeReference<Map<String, Integer>>() {
                }));
                LOGGER.info("Stock loaded successfully from file.");
            }
        } catch (IOException e) {
            LOGGER.error("Error loading stock data: {}", e.getMessage());
        }
    }

    public synchronized void saveStock() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFileStock, productStock);
            LOGGER.info("Stock saved successfully to file.");
        } catch (IOException e) {
            LOGGER.error("Error saving stock data: {}", e.getMessage(), e);
        }
    }

    public void addStockInWarehouse(String productKey, int quantity) {
        Product product = productService.getProductByKey(productKey);
        if (product == null) {
            throw new ProductDoesNotExistException("Product can't be null");
        }

        productStock.put(productKey, productStock.getOrDefault(productKey, 0) + quantity);
        LOGGER.info("Added {} units of product {} to stock.", quantity, productKey);

        try {
            productService.deactivateProduct(product);
        } catch (ProductAlreadyDeactivatedException err) {
            //
        }

        saveStock();
    }

    public void reduceStock(String productName, int quantity) {
        if (!productStock.containsKey(productName) || productStock.get(productName) < quantity) {
            LOGGER.error("Not enough stock of product: {}, available stock: {}, requested: {} ", productName,
                productStock.getOrDefault(productName, 0), quantity);
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

    public String getStock() {
        StringBuilder report = new StringBuilder();

        report.append("Product Stock List:\n")
            .append(BLUE).append("----------------------------").append(RESET).append("\n");

        for (Map.Entry<String, Integer> entry : productStock.entrySet()) {
            String product = capitalizeWords(entry.getKey().replaceAll("_", " "));
            int stock = entry.getValue();

            report.append(String.format(
                BLUE + "- " + RESET + "%-30s %sStock: %-3d%s" + BLUE + "   KEY" + RESET + ":%s\n",
                product,
                GREEN, stock,
                RESET, entry.getKey()
            ));
        }

        report.append(BLUE).append("----------------------------").append(RESET).append("\n");

        return report.toString();
    }

    public String getCatalogProduct(String productName) {
        if (productName.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        return switch (productName) {
            case "pizza" ->
                getCatalog("Pizza Catalog", "large, medium, small", PizzaType.values(), "pizza_", BLUE, true);
            case "drink" -> getCatalog("Drink Catalog", "large, small", DrinkType.values(), "drink_", RED, true);
            case "sauce" -> getCatalog("Sauce Catalog", "only small ", SauceType.values(), "sauce_", GREEN, false);
            default -> "No such product!";
        };

    }

    private <T extends Enum<T>> String getCatalog(String title, String sizeInfo, T[] values, String keyPrefix,
                                                  String color, boolean size) {
        StringBuilder catalog = new StringBuilder();

        catalog.append(color).append(title).append("\n").append(RESET)
            .append("----------------------------\n")
            .append("Available sizes: ")
            .append(sizeInfo)
            .append(color).append("\n----------------------------\n").append(RESET);

        for (T value : values) {
            String productName = capitalizeWords(value.name().replaceAll("_", " ").toLowerCase());

            if (size) {
                catalog.append(String.format(
                    "%s-%s %-15s %sKEY_EXAMPLE:%s %s_small\n",
                    color, RESET, productName,
                    color, RESET, keyPrefix + value.name().toLowerCase()
                ));
            } else {
                catalog.append(String.format(
                    "%s-%s %-15s %sKEY_EXAMPLE:%s %s\n",
                    color, RESET, productName,
                    color, RESET, keyPrefix + value.name().toLowerCase()
                ));
            }
        }

        catalog.append(color).append("----------------------------").append(RESET).append("\n");
        return catalog.toString();
    }

    private String capitalizeWords(String input) {
        return Arrays.stream(input.split("_"))
            .filter(word -> !word.isEmpty())
            .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
            .collect(Collectors.joining(" "));
    }

    Map<String, Integer> getProductStock() {
        return productStock;
    }
}
