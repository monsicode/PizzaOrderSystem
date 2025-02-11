package com.deliciouspizza.repository;

import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.entity.product.Sauce;
import com.deliciouspizza.enums.DrinkType;
import com.deliciouspizza.enums.DrinkVolume;
import com.deliciouspizza.enums.PizzaSize;
import com.deliciouspizza.enums.PizzaType;
import com.deliciouspizza.enums.SauceType;
import com.deliciouspizza.exception.ProductAlreadyActiveException;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProductRepository {
    protected String filePathInactiveProducts = "data-storage/inactiveProduct.json";
    private String filePathActiveProducts = "data-storage/activeProducts.json";
    private File jsonFileActive = new File(filePathActiveProducts);
    private File jsonFileInactive = new File(filePathInactiveProducts);

    private static final int MIN_KEY_PARTS = 2;

    private static final Logger LOGGER = LogManager.getLogger(ProductRepository.class);

    private Map<String, Product> inactiveProducts;
    private Map<String, Product> activeProducts;
    private final ObjectMapper objectMapper;

    public ProductRepository(ObjectMapper objectMapper, File jsonFileActive, Map<String, Product> activeProducts,
                             File jsonFileInactive, Map<String, Product> inactiveProducts) {
        this.objectMapper = objectMapper;
        this.jsonFileActive = jsonFileActive;
        this.jsonFileInactive = jsonFileInactive;
        this.activeProducts = activeProducts;
        this.inactiveProducts = inactiveProducts;
    }

    public ProductRepository() {
        inactiveProducts = new ConcurrentHashMap<>();
        activeProducts = new ConcurrentHashMap<>();
        objectMapper = new ObjectMapper();

        loadActiveProducts();
        loadInActiveProducts();
    }

    public synchronized void loadActiveProducts() {
        try {
            if (jsonFileActive.length() == 0) {
                activeProducts = new ConcurrentHashMap<>();
            } else {
                activeProducts = objectMapper.readValue(jsonFileActive, new TypeReference<>() {
                });
            }
        } catch (IOException e) {
            LOGGER.error("Error loading active products", e);
        }
    }

    public synchronized void loadInActiveProducts() {
        try {
            if (jsonFileInactive.length() == 0) {
                inactiveProducts = new ConcurrentHashMap<>();
            } else {
                inactiveProducts = objectMapper.readValue(jsonFileInactive,
                    new TypeReference<>() {
                    });
            }
        } catch (IOException e) {
            LOGGER.error("Error loading inactive products", e);
        }
    }

    protected synchronized void saveProducts(String path, Map<String, Product> productsMap) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), productsMap);
        } catch (IOException e) {
            LOGGER.error("Error saving products to file: {}", path);
        }
    }

    public Map<String, Product> getAllActiveProducts() {
        return Collections.unmodifiableMap(activeProducts);
    }

    public Map<String, Product> getAllInactiveProducts() {
        return Collections.unmodifiableMap(inactiveProducts);
    }

    public void activateProduct(Product product) throws ProductAlreadyActiveException {
        try {
            productValidation(product);
        } catch (ProductDoesNotExistException err) {
            LOGGER.error("Product validation failed: {}", err.getMessage());
            return;
        }

        String key = product.generateKey();

        if (getActiveProduct(key) != null) {
            throw new ProductAlreadyActiveException("The product is already active!");
        }

        product.activateProduct();
        inactiveProducts.remove(key);
        activeProducts.put(key, product);
        saveProducts(filePathActiveProducts, activeProducts);
        saveProducts(filePathInactiveProducts, inactiveProducts);
        LOGGER.info("Product activated successfully: {}", product.generateKey());
    }

    public void deactivateProduct(Product product) throws ProductAlreadyDeactivatedException {
        try {
            productValidation(product);
        } catch (ProductDoesNotExistException err) {
            LOGGER.error("Product validation when deactivating product failed: {}", err.getMessage());
            return;
        }

        String key = product.generateKey();

        if (getInactiveProduct(key) != null) {
            throw new ProductAlreadyDeactivatedException("The product is already deactivated!");
        }

        product.deactivateProduct();
        activeProducts.remove(key);
        inactiveProducts.put(key, product);
        saveProducts(filePathActiveProducts, activeProducts);
        saveProducts(filePathInactiveProducts, inactiveProducts);
        LOGGER.info("Product deactivated successfully: {}", product.generateKey());
    }

    public Product getActiveProduct(String productName) {
        return activeProducts.get(productName);
    }

    public Product getInactiveProduct(String productName) {
        return inactiveProducts.get(productName);
    }

    //modified
    public Product getProduct(String productName) {
        if (inactiveProducts.containsKey(productName)) {
            return inactiveProducts.get(productName);

        } else if (activeProducts.containsKey(productName)) {
            return activeProducts.get(productName);

        } else {
            LOGGER.warn("Product {} doesn't exist", productName);
            throw new ProductDoesNotExistException("Product doesn't exist!");
        }
    }

    public boolean isProductActive(String productKey) {
        return activeProducts.containsKey(productKey);
    }

    public boolean isProductInactive(String productKey) {
        return inactiveProducts.containsKey(productKey);
    }

    public boolean isItGoodForUnderAgedCustomers(String key) {
        Product product = activeProducts.get(key);
        productValidation(product);

        return product.isItGoodForUnderAgedCustomers();
    }

    private void productValidation(Product product) {
        if (product == null) {
            throw new ProductDoesNotExistException("Product doesn't exist");
        }
    }

    //catch the exception in AddStockToWarehouse command
    public Product createProductFromKey(String key) {
        List<String> validSizes = Arrays.asList("small", "medium", "large");
        String[] parts = key.split("_");

        if (parts.length < MIN_KEY_PARTS) {
            throw new IllegalArgumentException("Invalid key format");
        }

        String product = parts[0];
        String size = null;
        String type = "";

        if (validSizes.contains(parts[parts.length - 1])) {
            size = parts[parts.length - 1];
            type = String.join("_",
                Arrays.copyOfRange(parts, 1, parts.length - 1));
        } else {
            type = String.join("_", Arrays.copyOfRange(parts, 1, parts.length));
        }

        validateProductData(product, type);

        return switch (product) {
            case "pizza" -> new Pizza(PizzaType.valueOf(type.toUpperCase()), PizzaSize.valueOf(size.toUpperCase()));
            case "drink" -> new Drink(DrinkType.valueOf(type.toUpperCase()), DrinkVolume.valueOf(size.toUpperCase()));
            case "sauce" -> new Sauce(SauceType.valueOf(type.toUpperCase()));
            default -> throw new IllegalArgumentException("Unknown product type: " + product);
        };

    }

    private void validateProductData(String product, String productType) {
        switch (product) {
            case "pizza" -> {
                if (!PizzaType.isValid(productType)) {
                    throw new IllegalArgumentException("Invalid pizza type: " + productType);
                }
            }
            case "drink" -> {
                if (!DrinkType.isValid(productType)) {
                    throw new IllegalArgumentException("Invalid drink type: " + productType);
                }
            }
            case "sauce" -> {
                if (!SauceType.isValid(productType)) {
                    throw new IllegalArgumentException("Invalid sauce type: " + productType);
                }
            }
            default -> throw new IllegalArgumentException("Unknown product type: " + product);
        }
    }

}