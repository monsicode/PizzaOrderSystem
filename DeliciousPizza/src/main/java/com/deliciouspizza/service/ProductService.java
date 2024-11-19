package com.deliciouspizza.service;

import com.deliciouspizza.Singleton;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.ProductAlreadyActiveException;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.repository.ProductRepository;

import java.util.Map;

public class ProductService {

    private static final ProductRepository PRODUCT_REPOSITORY = Singleton.getInstance(ProductRepository.class);

    public Map<String, Product> getAllActiveProducts() {
        return PRODUCT_REPOSITORY.getAllActiveProducts();
    }

    public Map<String, Product> getAllInactiveProducts() {
        return PRODUCT_REPOSITORY.getAllInactiveProducts();
    }

    public void addNewProduct(Product product) {
        try {
            PRODUCT_REPOSITORY.addProduct(product);
        } catch (IllegalArgumentException err) {
            System.out.println(err.getMessage());
        }
    }

    public void deactivateProduct(Product product) {
        try {
            PRODUCT_REPOSITORY.deactivateProduct(product);
        } catch (ProductAlreadyDeactivatedException | IllegalArgumentException err) {
            System.out.println(err.getMessage());
        }
    }

    public void activateProduct(Product product) {
        try {
            PRODUCT_REPOSITORY.activateProduct(product);
        } catch (ProductAlreadyActiveException | IllegalArgumentException err) {
            System.out.println(err.getMessage());
        }
    }

    public double getProductPriceByKey(String productKey) {
        try {
            Product product = PRODUCT_REPOSITORY.getProduct(productKey);
            return product.calculatePrice();
        } catch (ProductDoesNotExistException err) {
            System.out.println(err.getMessage());
        }
        return 0;
    }


    //do we need this methods ?
    public Product getActiveProduct(String product) {
        return PRODUCT_REPOSITORY.getActiveProduct(product);
    }

    public Product getInactiveProduct(String product) {
        return PRODUCT_REPOSITORY.getInactiveProduct(product);
    }

}
