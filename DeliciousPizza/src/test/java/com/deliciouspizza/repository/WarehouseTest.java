package com.deliciouspizza.repository;

import com.deliciouspizza.entity.order.Order;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class WarehouseTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProductService productService;

    private Warehouse warehouse;

    @TempDir
    Path tempDir;

    private Map<String, Integer> productStock;
    private File tempFileStock;

    @BeforeEach
    void setUp() throws IOException {
        productStock = new ConcurrentHashMap<>();
        tempFileStock = tempDir.resolve("stock.json").toFile();
        String jsonContent = """
            {
              "pizza_pepperoni_small": 10,
              "drink_cola_large": 5
            }
            """;
        Files.write(tempFileStock.toPath(), jsonContent.getBytes());
        productStock.put("pizza_pepperoni_small", 10);
        productStock.put("drink_cola_large", 5);
        warehouse = new Warehouse(objectMapper, productService, tempFileStock);
    }

    @Test
    void testLoadStock() {
        warehouse = new Warehouse(new ObjectMapper(), productService, tempFileStock);
        productStock.put("pizza_pepperoni_small", 10);
        productStock.put("drink_cola_large", 5);

        warehouse.loadStock();

        assertEquals(productStock, warehouse.getProductStock());
    }

    @Test
    void testLoadStockWithEmptyFile() throws IOException {
        warehouse = new Warehouse(new ObjectMapper(), null, new File(""));
        warehouse.loadStock();

        assertTrue(warehouse.getProductStock().isEmpty());
    }

    @Test
    void testLoadStockWithIOException() throws IOException {
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenThrow(
            new IOException("Test IO Exception"));

        try (LogCaptor logCaptor = LogCaptor.forClass(Warehouse.class)) {
            warehouse.loadStock();
            assertTrue(logCaptor.getErrorLogs().contains("Error loading stock data: Test IO Exception"));
        }
    }

    @Test
    void testSaveStock() throws IOException {
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(tempFileStock, warehouse.getProductStock());

        warehouse.saveStock();

        verify(objectWriter, times(1)).writeValue(tempFileStock, warehouse.getProductStock());
    }

    @Test
    void testSaveStockWithIOException() throws IOException {
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doThrow(new IOException("Test IO Exception")).when(objectWriter)
            .writeValue(tempFileStock, warehouse.getProductStock());

        try (LogCaptor logCaptor = LogCaptor.forClass(Warehouse.class)) {
            warehouse.saveStock();
            assertTrue(logCaptor.getErrorLogs().contains("Error saving stock data: Test IO Exception"));
        }
    }

    @Test
    void testAddStockInWarehouse() throws ProductAlreadyDeactivatedException, IOException {
        String productKey = "pizza_pepperoni_small";
        int quantity = 5;

        Product product = mock(Product.class);
        when(productService.getProductByKey(productKey)).thenReturn(product);

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(productStock);

        warehouse.loadStock();
        warehouse.addStockInWarehouse(productKey, quantity);

        assertEquals(15, warehouse.getProductStock().get(productKey));
        verify(productService, times(1)).deactivateProduct(product);
    }

    @Test
    void testAddStockInWarehouseWithNonExcistingProduct() {
        assertThrows(ProductDoesNotExistException.class, () -> warehouse.addStockInWarehouse("prod", 1));
    }

//    @Test
//    void testAddStockInWarehouseWithAlreadyDeactivatedProduct() throws IOException, ProductAlreadyDeactivatedException {
//        String productKey = "pizza_pepperoni_small";
//        int quantity = 5;
//
//        Product product = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
//        product.deactivateProduct();
//
//        when(productService.getProductByKey(productKey)).thenReturn(product);
//
//        ObjectWriter objectWriter = mock(ObjectWriter.class);
//        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
//        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(productStock);
//
//        warehouse.loadStock();
//        warehouse.addStockInWarehouse(productKey, quantity);
//
//        assertEquals(15, warehouse.getProductStock().get(productKey));
//        verify(productService, times(1)).deactivateProduct(product);
//    }

    @Test
    void testReduceStock() throws IOException {
        String productKey = "pizza_pepperoni_small";
        int quantity = 5;

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(productStock);

        warehouse.loadStock();
        warehouse.reduceStock(productKey, quantity);

        assertEquals(5, warehouse.getProductStock().get(productKey));
    }

    @Test
    void testReduceStockWithInsufficientQuantityStock() throws IOException {
        String productKey = "pizza_pepperoni_small";
        int quantity = 15;

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(productStock);

        warehouse.loadStock();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            warehouse.reduceStock(productKey, quantity);
        });

        assertEquals("Not enough stock of product: " + productKey, exception.getMessage());
    }

    @Test
    void testReduceStockWithExactQuantity() throws IOException {
        String productKey = "pizza_pepperoni_small";
        int quantity = 10;

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(productStock);

        warehouse.loadStock();
        warehouse.reduceStock(productKey, quantity);

        assertFalse(warehouse.getProductStock().containsKey(productKey));
    }

    @Test
    void testReduceStockWithNonExistingProduct() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> warehouse.reduceStock("baba", 11),
            "The product doesn't exist, should throw exception");
    }


    @Test
    void testReduceStockWithOrder() throws IOException {
        Order order = mock(Order.class);
        when(order.getOrder()).thenReturn(Map.of("pizza_pepperoni_small", 5, "drink_cola_large", 2));

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(productStock);

        warehouse.loadStock();

        warehouse.reduceStockWithOrder(order);

        assertEquals(5, warehouse.getProductStock().get("pizza_pepperoni_small"));
        assertEquals(3, warehouse.getProductStock().get("drink_cola_large"));
    }

    @Test
    void testReduceStockWithOrderWithOneInvalidProduct() throws IOException {
        Order order = mock(Order.class);
        when(order.getOrder()).thenReturn(Map.of("pizza_pepperoni", 5, "drink_cola_large", 2));

        assertThrows(IllegalArgumentException.class, () -> warehouse.reduceStockWithOrder(order));
    }

    @Test
    void testReduceStockWithOrderWithOneInsufficientQuantity() throws IOException {
        Order order = mock(Order.class);
        when(order.getOrder()).thenReturn(Map.of("pizza_pepperoni_small", 20, "drink_cola_large", 2));

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(productStock);
        warehouse.loadStock();

        assertThrows(IllegalArgumentException.class, () -> warehouse.reduceStockWithOrder(order));
    }

    @Test
    void testReduceStockWithOrderWithExactQuantityOfOneProduct() throws IOException {
        Order order = mock(Order.class);
        when(order.getOrder()).thenReturn(Map.of("pizza_pepperoni_small", 10, "drink_cola_large", 2));

        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(productStock);

        warehouse.loadStock();
        warehouse.reduceStockWithOrder(order);

        assertFalse(warehouse.getProductStock().containsKey("pizza_pepperoni_small"),
            "Product should be completely removed from map.");
    }

    @Test
    void testGetStock() throws IOException {
        String expectedOutput = """
            Product Stock List:
            [34m----------------------------[0m
            [34m- [0mPizza pepperoni small          [32mStock: 10 [0m[34m   KEY[0m:pizza_pepperoni_small
            [34m- [0mDrink cola large               [32mStock: 5  [0m[34m   KEY[0m:drink_cola_large
            [34m----------------------------[0m
            """;

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(productStock);
        warehouse.loadStock();

        String stockReport = warehouse.getStock();
        assertEquals(expectedOutput, stockReport);
    }

    @Test
    void testGetCatalogProductWithPizza() {
        String expectedPizzaCatalog = """
            [34mPizza Catalog
            [0m----------------------------
            Available sizes: large, medium, small[34m
            ----------------------------
            [0m[34m-[0m Margherita      [34mKEY_EXAMPLE:[0m pizza_margherita_small
            [34m-[0m Pepperoni       [34mKEY_EXAMPLE:[0m pizza_pepperoni_small
            [34m-[0m Hawaiian        [34mKEY_EXAMPLE:[0m pizza_hawaiian_small
            [34m-[0m Veggie          [34mKEY_EXAMPLE:[0m pizza_veggie_small
            [34m-[0m Bbq chicken     [34mKEY_EXAMPLE:[0m pizza_bbq_chicken_small
            [34m-[0m Four cheese     [34mKEY_EXAMPLE:[0m pizza_four_cheese_small
            [34m-[0m Seafood         [34mKEY_EXAMPLE:[0m pizza_seafood_small
            [34m-[0m Meat lovers     [34mKEY_EXAMPLE:[0m pizza_meat_lovers_small
            [34m----------------------------[0m
            """;
        String pizzaCatalog = warehouse.getCatalogProduct("pizza");
        assertEquals(expectedPizzaCatalog, pizzaCatalog);
    }

    @Test
    void testGetCatalogProductWithSauce() {
        String expectedPizzaCatalog = """
            [32mSauce Catalog
            [0m----------------------------
            Available sizes: only small [32m
            ----------------------------
            [0m[32m-[0m Tomato sauce    [32mKEY_EXAMPLE:[0m sauce_tomato_sauce
            [32m-[0m Garlic sauce    [32mKEY_EXAMPLE:[0m sauce_garlic_sauce
            [32m-[0m Bbq sauce       [32mKEY_EXAMPLE:[0m sauce_bbq_sauce
            [32m-[0m Spicy sauce     [32mKEY_EXAMPLE:[0m sauce_spicy_sauce
            [32m-[0m Ranch           [32mKEY_EXAMPLE:[0m sauce_ranch
            [32m-[0m Pesto           [32mKEY_EXAMPLE:[0m sauce_pesto
            [32m-[0m White sauce     [32mKEY_EXAMPLE:[0m sauce_white_sauce
            [32m-[0m Honey mustard   [32mKEY_EXAMPLE:[0m sauce_honey_mustard
            [32m----------------------------[0m
            """;

        String sauceCatalog = warehouse.getCatalogProduct("sauce");
        assertEquals(expectedPizzaCatalog, sauceCatalog);
    }

    @Test
    void testGetProductCatalogWithNoProduct() {
        assertThrows(IllegalArgumentException.class, () -> warehouse.getCatalogProduct(""));
    }

    @Test
    void testGetProductCatalogWithWrongProductName() {
        assertEquals("No such product!", warehouse.getCatalogProduct("baba"));
    }

}
