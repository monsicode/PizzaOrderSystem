package com.deliciouspizza.repository;

import com.deliciouspizza.entity.product.Drink;
import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.enums.DrinkType;
import com.deliciouspizza.enums.DrinkVolume;
import com.deliciouspizza.enums.PizzaSize;
import com.deliciouspizza.enums.PizzaType;
import com.deliciouspizza.exception.ProductAlreadyActiveException;
import com.deliciouspizza.exception.ProductAlreadyDeactivatedException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
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

import static com.deliciouspizza.enums.StatusProduct.INACTIVE;
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
public class ProductRepositoryTest {

    @Mock
    private ObjectMapper objectMapper;

    private ProductRepository productRepository;

    @TempDir
    Path tempDir;

    private Map<String, Product> activeProducts;
    private Map<String, Product> inactiveProducts;

    private File tempFileActive;
    private File tempFileInactive;

    @BeforeEach
    void setUp() throws IOException {

        activeProducts = new ConcurrentHashMap<>();
        inactiveProducts = new ConcurrentHashMap<>();

        tempFileActive = tempDir.resolve("activeProducts.json").toFile();
        String jsonContent = """
            {
              "pizza_pepperoni_small": {
                "type": "pizza",
                "pizzaType": "PEPPERONI",
                "pizzaSize": "SMALL",
                "statusProduct": "ACTIVE"
              }
            }
            """;
        Files.write(tempFileActive.toPath(), jsonContent.getBytes());

        tempFileInactive = tempDir.resolve("inactiveProducts.json").toFile();
        String jsonContent2 = """
            {
              "pizza_meat_lovers_large": {
                "type": "pizza",
                "pizzaType": "MEAT_LOVERS",
                "pizzaSize": "LARGE",
                "statusProduct": "INACTIVE"
              }
            }
            """;
        Files.write(tempFileInactive.toPath(), jsonContent2.getBytes());

        productRepository =
            new ProductRepository(objectMapper, tempFileActive, activeProducts, tempFileInactive, inactiveProducts);
    }

    @Test
    void testLoadActiveProducts() throws IOException {
        activeProducts.put("pizza_pepperoni_small", new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL));

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(activeProducts);

        productRepository.loadActiveProducts();

        verify(objectMapper, times(1)).readValue(any(File.class), any(TypeReference.class));
        assertTrue(productRepository.getAllActiveProducts().containsKey("pizza_pepperoni_small"));
    }

    @Test
    void testLoadActiveProductsWithEmptyFile() {
        File jsonFileActive = mock(File.class);
        when(jsonFileActive.length()).thenReturn(0L);

        productRepository = new ProductRepository(objectMapper, jsonFileActive, activeProducts, null, inactiveProducts);
        productRepository.loadActiveProducts();

        assertTrue(productRepository.getAllActiveProducts().isEmpty(),
            "The activeProducts map should be empty when the file is empty.");
    }

    @Test
    void testIOExceptionDuringLoadActiveProducts() throws IOException {
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenThrow(
            new IOException("Test IO Exception"));

        try (LogCaptor logCaptor = LogCaptor.forClass(ProductRepository.class)) {
            productRepository.loadActiveProducts();
            assertTrue(logCaptor.getErrorLogs().contains("Error loading active products"));
        }
    }

    @Test
    void testLoadInactiveProducts() throws IOException {
        inactiveProducts = new ConcurrentHashMap<>();
        inactiveProducts.put("pizza_meat_lovers_large", new Pizza(PizzaType.MEAT_LOVERS, PizzaSize.LARGE));

        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenReturn(inactiveProducts);

        productRepository.loadInActiveProducts();

        verify(objectMapper, times(1)).readValue(any(File.class), any(TypeReference.class));
        assertTrue(productRepository.getAllInactiveProducts().containsKey("pizza_meat_lovers_large"));
    }

    @Test
    void testLoadInactiveProductsWithEmptyFile() {
        File jsonFileInactive = mock(File.class);
        when(jsonFileInactive.length()).thenReturn(0L);

        productRepository =
            new ProductRepository(objectMapper, null, new ConcurrentHashMap<>(), jsonFileInactive, inactiveProducts);
        productRepository.loadInActiveProducts();

        assertTrue(productRepository.getAllInactiveProducts().isEmpty(),
            "The inactiveProducts map should be empty when the file is empty.");
    }

    @Test
    void testIOExceptionDuringLoadInactiveProducts() throws IOException {
        when(objectMapper.readValue(any(File.class), any(TypeReference.class))).thenThrow(
            new IOException("Test IO Exception"));

        try (LogCaptor logCaptor = LogCaptor.forClass(ProductRepository.class)) {
            productRepository.loadInActiveProducts();
            assertTrue(logCaptor.getErrorLogs().contains("Error loading inactive products"));
        }
    }

    @Test
    void testActivateProduct() throws ProductAlreadyActiveException, IOException {
        Product product = new Pizza(PizzaType.MEAT_LOVERS, PizzaSize.LARGE);
        String key = product.generateKey();

        //to not write in real file
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        inactiveProducts.put(key, product);
        productRepository.activateProduct(product);

        assertTrue(activeProducts.containsKey(key));
        assertFalse(inactiveProducts.containsKey(key));

    }

    @Test
    void testActivateProductWithAlreadyActiveProduct() throws ProductAlreadyActiveException, IOException {
        Product product = new Pizza(PizzaType.MEAT_LOVERS, PizzaSize.LARGE);
        String key = product.generateKey();

        activeProducts.put(key, product);

        assertThrows(ProductAlreadyActiveException.class, () -> productRepository.activateProduct(product));
    }

    @Test
    void testActivateProductWithNonExcistingProduct() throws ProductAlreadyActiveException {
        Product product = null;

        try (LogCaptor logCaptor = LogCaptor.forClass(ProductRepository.class)) {
            productRepository.activateProduct(product);
            assertTrue(logCaptor.getErrorLogs().contains("Product validation failed: Product doesn't exist"));
        }
    }

    @Test
    void testDeactivateProduct() throws IOException, ProductAlreadyDeactivatedException {
        Product product = new Pizza(PizzaType.MEAT_LOVERS, PizzaSize.LARGE);
        String key = product.generateKey();

        //to not write in real file
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        doNothing().when(objectWriter).writeValue(any(File.class), any());

        activeProducts.put(key, product);
        productRepository.deactivateProduct(product);

        assertTrue(inactiveProducts.containsKey(key));
        assertFalse(activeProducts.containsKey(key));
    }

    @Test
    void testDeactivateProductWithAlreadyDeactivatedProduct() throws ProductAlreadyActiveException, IOException {
        Product product = new Pizza(PizzaType.MEAT_LOVERS, PizzaSize.LARGE);
        String key = product.generateKey();

        inactiveProducts.put(key, product);

        assertThrows(ProductAlreadyDeactivatedException.class, () -> productRepository.deactivateProduct(product));
    }

    @Test
    void testDeactivateProductWithNonExcistingProduct() throws ProductAlreadyDeactivatedException {
        Product product = null;

        try (LogCaptor logCaptor = LogCaptor.forClass(ProductRepository.class)) {
            productRepository.deactivateProduct(product);
            assertTrue(logCaptor.getErrorLogs()
                .contains("Product validation when deactivating product failed: Product doesn't exist"));
        }
    }

    @Test
    void testGetProductFromInactiveProducts() {
        inactiveProducts.put("pizza_meat_lovers_large", new Pizza(PizzaType.MEAT_LOVERS, PizzaSize.LARGE));

        Product product = new Pizza(PizzaType.MEAT_LOVERS, PizzaSize.LARGE);

        assertEquals(product, productRepository.getProduct("pizza_meat_lovers_large"));
        assertEquals(INACTIVE, productRepository.getProduct("pizza_meat_lovers_large").getStatusProduct());
    }

    @Test
    void testGetProductFromActiveProducts() {
        Product product = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        product.activateProduct();
        activeProducts.put("pizza_pepperoni_small", product);

        Product product2 = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        product2.activateProduct();

        assertEquals(product2, productRepository.getProduct("pizza_pepperoni_small"));
        assertEquals(product2.getStatusProduct(),
            productRepository.getProduct("pizza_pepperoni_small").getStatusProduct());
    }

    @Test
    void testGetProductWithNonExcistingProduct() {
        assertThrows(ProductDoesNotExistException.class, () -> productRepository.getProduct("baba"));
    }

    @Test
    void testIsProductActive() {
        Product product = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        activeProducts.put("pizza_pepperoni_small", product);

        assertTrue(productRepository.isProductActive("pizza_pepperoni_small"));

    }

    @Test
    void testIsProductActiveWithFalseReturn() {
        Product product = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        inactiveProducts.put("pizza_pepperoni_small", product);

        assertFalse(productRepository.isProductActive("pizza_pepperoni_small"));
    }

    @Test
    void testIsProductInactive() {
        Product product = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        inactiveProducts.put("pizza_pepperoni_small", product);

        assertTrue(productRepository.isProductInactive("pizza_pepperoni_small"));

    }

    @Test
    void testIsProductInactiveWithFalseReturn() {
        Product product = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        activeProducts.put("pizza_pepperoni_small", product);

        assertFalse(productRepository.isProductInactive("pizza_pepperoni_small"));
    }


    @Test
    void testIsItGoodForUnderAgedCustomers() {
        Product product = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        activeProducts.put("pizza_pepperoni_small", product);
        assertTrue(productRepository.isItGoodForUnderAgedCustomers("pizza_pepperoni_small"));
    }

    @Test
    void testIsItGoodForUnderAgedCustomersWithAlcoholicDrink() {
        Drink drink = new Drink(DrinkType.BEER, DrinkVolume.SMALL);
        activeProducts.put("drink_beer_small", drink);
        assertFalse(productRepository.isItGoodForUnderAgedCustomers("drink_beer_small"));
    }

    @Test
    void testIsItGoodForUnderAgedCustomersWhenNotInActiveProducts() {
        Product product = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);
        inactiveProducts.put("pizza_pepperoni_small", product);
        assertThrows(ProductDoesNotExistException.class,
            () -> productRepository.isItGoodForUnderAgedCustomers("pizza_pepperoni_small"));
    }

    @Test
    void testSavingProductWithException() throws IOException {
        ObjectWriter objectWriter = mock(ObjectWriter.class);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);

        doThrow(new IOException("Test IO Exception")).when(objectWriter).writeValue(any(File.class), any());

        try (LogCaptor logCaptor = LogCaptor.forClass(ProductRepository.class)) {
            productRepository.saveProducts("path", activeProducts);
            assertTrue(logCaptor.getErrorLogs().contains("Error saving products to file: path"));
        }
    }

}
