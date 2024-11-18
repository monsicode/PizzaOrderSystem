package com.deliciouspizza.repository;

import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.ProductAlreadyActiveException;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.utils.StatusProduct;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//exceptions --> catch-ed

public class ProductRepository {
    private static final String FILE_PATH_INACTIVE_PRODUCTS = "src/main/resources/inactiveProduct.json";
    private static final String FILE_PATH_ACTIVE_PRODUCTS = "src/main/resources/activeProducts.json";
    private final File jsonFileActive = new File(FILE_PATH_ACTIVE_PRODUCTS);
    private final File jsonFileInactive = new File(FILE_PATH_INACTIVE_PRODUCTS);


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

    public void loadActiveProducts() {
        try {
            if (jsonFileActive.length() == 0) {
                activeProducts = new HashMap<>();
            } else {
                activeProducts = objectMapper.readValue(jsonFileActive, new TypeReference<Map<String, Product>>() {
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading active products!");
        }
    }

    public void loadInActiveProducts() {
        try {
            if (jsonFileInactive.length() == 0) {
                inactiveProducts = new HashMap<>();
            } else {
                inactiveProducts = objectMapper.readValue(jsonFileInactive,
                    new TypeReference<Map<String, Product>>() {
                    });
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading inactive products!");
        }
    }

    //Write Map to JSON file
    private void saveProducts(String path, Map<String, Product> productsMap) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), productsMap);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving products!");
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
            System.out.println(err.getMessage());
            return;
        }

        String key = product.generateKey();

        if (getActiveProduct(key) != null) {
            throw new ProductAlreadyActiveException("The product is already active!");
        }

        product.activateProduct();
        inactiveProducts.remove(key);
        activeProducts.put(key, product);
        saveProducts(FILE_PATH_ACTIVE_PRODUCTS, activeProducts);
        saveProducts(FILE_PATH_INACTIVE_PRODUCTS, inactiveProducts);
        System.out.println("Product activated successfully!");

    }

    public void deactivateProduct(Product product) throws ProductAlreadyDeactivatedException {
        try {
            productValidation(product);
        } catch (ProductDoesNotExistException err) {
            System.out.println(err.getMessage());
            return;
        }

        String key = product.generateKey();

        if (getInactiveProduct(key) != null) {
            throw new ProductAlreadyDeactivatedException("The product is already deactivated!");
        }

        product.deactivateProduct();
        activeProducts.remove(key);
        inactiveProducts.put(key, product);
        saveProducts(FILE_PATH_ACTIVE_PRODUCTS, activeProducts);
        saveProducts(FILE_PATH_INACTIVE_PRODUCTS, inactiveProducts);
        System.out.println("Product deactivated successfully!");
    }

    public Product getActiveProduct(String productName) {
        return activeProducts.get(productName);
    }

    public Product getInactiveProduct(String productName) {
        return inactiveProducts.get(productName);
    }

    public void addProduct(Product product) {
        // We add the product to the Map, only if the key is unique
        String key = product.generateKey();

        //Checks to add in inactive.json
        if (!inactiveProducts.containsKey(key) && product.getStatusProduct() == StatusProduct.INACTIVE) {
            inactiveProducts.put(key, product);
            saveProducts(FILE_PATH_INACTIVE_PRODUCTS, inactiveProducts);
            System.out.println("Product added successfully!");

            //Checks to add in active.json
        } else if (product.getStatusProduct() == StatusProduct.ACTIVE && !activeProducts.containsKey(key)) {
            activeProducts.put(key, product);
            saveProducts(FILE_PATH_ACTIVE_PRODUCTS, activeProducts);
            System.out.println("Product added successfully!");

        } else {
            throw new IllegalArgumentException("Product can't be added, because it already exist!");
        }
    }

    public Product getProduct(String productName) {
        if (inactiveProducts.containsKey(productName)) {
            return inactiveProducts.get(productName);

        } else if (activeProducts.containsKey(productName)) {
            return activeProducts.get(productName);

        } else {
            throw new ProductDoesNotExistException("Product doesn't exist!");
        }
    }

    public boolean isProductActive(String productKey) {
        return activeProducts.containsKey(productKey);
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

}