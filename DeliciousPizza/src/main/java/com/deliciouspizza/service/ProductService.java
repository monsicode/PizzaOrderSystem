package com.deliciouspizza.service;

import com.deliciouspizza.utils.Singleton;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.ProductAlreadyActiveException;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.repository.ProductRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ProductService {

    private static final Logger LOGGER = LogManager.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService() {
        productRepository = Singleton.getInstance(ProductRepository.class);
    }

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Map<String, Product> getAllActiveProducts() {
        return productRepository.getAllActiveProducts();
    }

    public Map<String, Product> getAllInactiveProducts() {
        return productRepository.getAllInactiveProducts();
    }

    public void deactivateProduct(Product product) throws ProductAlreadyDeactivatedException {
        productRepository.deactivateProduct(product);
    }

    public void activateProduct(Product product) throws ProductAlreadyActiveException {
        productRepository.activateProduct(product);
    }

    public double getProductPriceByKey(String productKey) {
        try {
            Product product = productRepository.getProduct(productKey);
            return product.calculatePrice();
        } catch (ProductDoesNotExistException err) {
            LOGGER.error(err.getMessage(), err);
        }
        return 0;
    }

    //catching the exception in the commands
    public Product getProductByKey(String productKey) {
        return productRepository.getProduct(productKey);
    }

    public Product createProductFromKey(String productKey) {
        return productRepository.createProductFromKey(productKey);
    }

    public boolean isItGoodForUnderAgedCustomers(String key) {
        return productRepository.isItGoodForUnderAgedCustomers(key);
    }

}
