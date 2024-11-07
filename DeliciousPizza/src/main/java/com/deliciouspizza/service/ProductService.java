package com.deliciouspizza.service;

import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.repository.ProductRepository;

import java.util.Map;

//catch here !

public class ProductService {
    private final ProductRepository productRepository;

    //TODO Singleton
    @SuppressWarnings("checkstyle:TodoComment")

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Map<String, Product> getActiveProducts() {
        return productRepository.getActiveProductsMap();
    }

    public void addNewProduct(Product product) {
        productRepository.addProduct(product);
    }

    public void deactivateProduct(Product product) {
        productRepository.deactivateProduct(product);
    }

    public double getProductPrice(Product product) {
        return product.calculatePrice();
    }

}
