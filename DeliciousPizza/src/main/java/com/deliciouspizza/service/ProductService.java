package com.deliciouspizza.service;

import com.deliciouspizza.utils.Singleton;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.ProductAlreadyActiveException;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.repository.ProductRepository;

import java.util.Map;

public class ProductService {

    private final ProductRepository productRepository = Singleton.getInstance(ProductRepository.class);

    public Map<String, Product> getAllActiveProducts() {
        return productRepository.getAllActiveProducts();
    }

    public Map<String, Product> getAllInactiveProducts() {
        return productRepository.getAllInactiveProducts();
    }

    public void addNewProduct(Product product) {
        try {
            productRepository.addProduct(product);
        } catch (IllegalArgumentException err) {
            System.out.println(err.getMessage());
        }
    }

    public void deactivateProduct(Product product) {
        try {
            productRepository.deactivateProduct(product);
        } catch (ProductAlreadyDeactivatedException | IllegalArgumentException err) {
            System.out.println(err.getMessage());
        }
    }

    public void activateProduct(Product product) {
        try {
            productRepository.activateProduct(product);
        } catch (ProductAlreadyActiveException | IllegalArgumentException err) {
            System.out.println(err.getMessage());
        }
    }

    public double getProductPriceByKey(String productKey) {
        try {
            Product product = productRepository.getProduct(productKey);
            return product.calculatePrice();
        } catch (ProductDoesNotExistException err) {
            System.out.println(err.getMessage());
        }
        return 0;
    }

    //do we need this methods ?
    public Product getActiveProduct(String product) {
        return productRepository.getActiveProduct(product);
    }

    public Product getInactiveProduct(String product) {
        return productRepository.getInactiveProduct(product);
    }

    //catching the exception in EmplpoyeeImpl
    public Product getProductByKey(String productKey) {
        return productRepository.getProduct(productKey);
    }

}
