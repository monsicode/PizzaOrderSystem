package com.deliciouspizza.service;

import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.ProductAlreadyActiveException;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.repository.ProductRepository;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    private Map<String, Product> activeProducts;
    private Map<String, Product> inactiveProducts;

    @BeforeEach
    void setUp() {
        activeProducts = new ConcurrentHashMap<>();
        inactiveProducts = new ConcurrentHashMap<>();

        productService = new ProductService(productRepository);
    }

    @Test
    void testGetAllActiveProducts() {
        Product product = mock(Product.class);
        activeProducts.put("pizza_pepperoni_small", product);

        when(productRepository.getAllActiveProducts()).thenReturn(activeProducts);

        Map<String, Product> result = productService.getAllActiveProducts();

        assertEquals(activeProducts, result);
        verify(productRepository, times(1)).getAllActiveProducts();
    }

    @Test
    void testGetAllInactiveProducts() {
        Product product = mock(Product.class);
        inactiveProducts.put("pizza_pepperoni_small", product);

        when(productRepository.getAllInactiveProducts()).thenReturn(inactiveProducts);

        Map<String, Product> result = productService.getAllInactiveProducts();

        assertEquals(inactiveProducts, result);
        verify(productRepository, times(1)).getAllInactiveProducts();
    }

    @Test
    void testDeactivateProduct() throws ProductAlreadyDeactivatedException {
        Product product = mock(Product.class);
        productService.deactivateProduct(product);

        verify(productRepository, times(1)).deactivateProduct(product);
    }

    @Test
    void testActivateProduct() throws ProductAlreadyActiveException {
        Product product = mock(Product.class);
        productService.activateProduct(product);

        verify(productRepository, times(1)).activateProduct(product);
    }

    @Test
    void testGetProductPriceByKey() throws ProductDoesNotExistException {
        String productKey = "pizza_pepperoni_small";
        Product product = mock(Product.class);
        when(product.calculatePrice()).thenReturn(10.0);
        when(productRepository.getProduct(productKey)).thenReturn(product);

        double price = productService.getProductPriceByKey(productKey);

        assertEquals(10.0, price);
        verify(productRepository, times(1)).getProduct(productKey);
    }

    @Test
    void testGetProductPriceByKeyWithNonExistentProduct() throws ProductDoesNotExistException {
        String productKey = "non_existent_product";
        when(productRepository.getProduct(productKey)).thenThrow(
            new ProductDoesNotExistException("Product does not exist"));

        try (LogCaptor logCaptor = LogCaptor.forClass(ProductService.class)) {
            double price = productService.getProductPriceByKey(productKey);
            assertEquals(0.0, price);
            assertTrue(logCaptor.getErrorLogs().contains("Product does not exist"));
        }
    }

    @Test
    void testGetProductByKey() throws ProductDoesNotExistException {
        String productKey = "pizza_pepperoni_small";
        Product product = mock(Product.class);
        when(productRepository.getProduct(productKey)).thenReturn(product);

        Product result = productService.getProductByKey(productKey);

        assertEquals(product, result);
        verify(productRepository, times(1)).getProduct(productKey);
    }

    @Test
    void testGetProductByKeyWithNonExistentProduct() throws ProductDoesNotExistException {
        String productKey = "non_existent_product";
        when(productRepository.getProduct(productKey)).thenThrow((ProductDoesNotExistException.class));
        assertThrows(ProductDoesNotExistException.class, () -> productService.getProductByKey(productKey));
    }

    @Test
    void testIsItGoodForUnderAgedCustomers() {
        String productKey = "pizza_pepperoni_small";
        when(productRepository.isItGoodForUnderAgedCustomers(productKey)).thenReturn(true);

        boolean result = productService.isItGoodForUnderAgedCustomers(productKey);

        assertTrue(result);
        verify(productRepository, times(1)).isItGoodForUnderAgedCustomers(productKey);
    }
}