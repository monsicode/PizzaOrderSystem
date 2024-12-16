package com.deliciouspizza.entity.order;

import com.deliciouspizza.entity.product.Pizza;
import com.deliciouspizza.enums.PizzaSize;
import com.deliciouspizza.enums.PizzaType;
import com.deliciouspizza.exception.InactiveProductException;
import com.deliciouspizza.exception.ProductDoesNotExistException;
import com.deliciouspizza.exception.ProductNotInOrderException;
import com.deliciouspizza.repository.ProductRepository;
import com.deliciouspizza.repository.Warehouse;
import com.deliciouspizza.utils.Singleton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderTest {
    @Mock
    private ProductRepository repository;

    @Mock
    private Warehouse warehouse;

    private MockedStatic<Singleton> mockedSingleton;

    private Order order;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        warehouse = mock(Warehouse.class);

        mockedSingleton = mockStatic(Singleton.class);
        mockedSingleton.when(() -> Singleton.getInstance(ProductRepository.class)).thenReturn(repository);
        mockedSingleton.when(() -> Singleton.getInstance(Warehouse.class)).thenReturn(warehouse);

        order = new Order("test");
    }

    @Test
    void testAddProductNullProductKey() {

        assertThrows(IllegalArgumentException.class, () -> order.addProduct(null, 1),
            "Adding product should be illegal with null product key");
    }

    @Test
    void testAddProductBlankProductKey() {
        assertThrows(IllegalArgumentException.class, () -> order.addProduct(" ", 1),
            "Adding product should be illegal with blank product key");
    }

    @Test
    void testAddProductNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> order.addProduct("abcd", -1),
            "Adding product should be illegal with negative quantity");
    }

    @Test
    void testAddProductZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () -> order.addProduct("abcd", 0),
            "Adding product should be illegal with zero quantity");
    }

    @Test
    void testAddProductInactiveProduct() {
        assertThrows(InactiveProductException.class, () -> order.addProduct("abcd", 1),
            "Adding product should be illegal if the product is not active");
    }

    @Test
    void testAddProductInvalidProductKey() {
        assertThrows(InactiveProductException.class, () -> order.addProduct("abcd", 1),
            "Adding product should be illegal if the product key is invalid");
    }

    @Test
    void testAddProductOrderPriceOfOrderWithOneQuantity() throws InactiveProductException {
        when(repository.getProduct("abcd"))
            .thenReturn(new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL));
        when(repository.isProductActive("abcd"))
            .thenReturn(true);

        order.addProduct("abcd", 1);

        assertEquals(8.0, order.getTotalPrice(),
            "Incorrect calculation of the price of the order");

        verify(warehouse, times(1)).reduceStock("abcd", 1);
    }

    @Test
    void testAddProductOrderPriceOfOrderWithThreeQuantity() throws InactiveProductException {
        when(repository.getProduct("abcd"))
            .thenReturn(new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL));
        when(repository.isProductActive("abcd"))
            .thenReturn(true);

        order.addProduct("abcd", 3);

        assertEquals(24.0, order.getTotalPrice(),
            "Incorrect calculation of the price of the order");

        verify(warehouse, times(1)).reduceStock("abcd", 3);
    }

    @Test
    void testRemoveProductNullProductKey() {
        assertThrows(IllegalArgumentException.class, () -> order.removeProduct(null, 1),
            "Incorrect exception throw in remove product when the product key is null");
    }

    @Test
    void testRemoveProductProductNotInOrder() {
        assertThrows(ProductNotInOrderException.class, () -> order.removeProduct("abcd", 1),
            "When removing a product an exception should be thrown if the product is not in the order");
    }

    @Test
    void testRemoveProductEmptyProductKey() {
        assertThrows(IllegalArgumentException.class, () -> order.removeProduct("  ", 1),
            "An IllegalArgumentException should be thrown when the product is empty/blank");
    }

    @Test
    void testRemoveProductNegativeQuantity() throws InactiveProductException {
        when(repository.getProduct("abcd")).thenReturn(new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL));
        when(repository.isProductActive("abcd")).thenReturn(true);

        order.addProduct("abcd", 2);

        assertThrows(IllegalArgumentException.class, () -> order.removeProduct("abcd", -1),
            "When removing product an exception should be thrown when the quantity is negative");
    }

    @Test
    void testRemoveProductBiggerQuantity() throws InactiveProductException {
        when(repository.getProduct("abcd")).thenReturn(new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL));
        when(repository.isProductActive("abcd")).thenReturn(true);

        order.addProduct("abcd", 2);

        assertThrows(IllegalArgumentException.class, () -> order.removeProduct("abcd", 5),
            "When removing product an exception should be thrown when the quantity is bigger than the available at warehouse");
    }

    @Test
    void testRemoveProductExactRemove() throws InactiveProductException, ProductNotInOrderException {
        when(repository.getProduct("abcd")).thenReturn(new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL));
        when(repository.isProductActive("abcd")).thenReturn(true);

        order.addProduct("abcd", 2);
        order.removeProduct("abcd", 2);

        assertEquals(0, order.getQuantityProduct("abcd"),
            "The product shouldn't be in the order when remove all quantity");
    }

    @Test
    void testRemoveProductSmallerAmount() throws InactiveProductException, ProductNotInOrderException {
        when(repository.getProduct("abcd")).thenReturn(new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL));
        when(repository.isProductActive("abcd")).thenReturn(true);

        order.addProduct("abcd", 2);
        order.removeProduct("abcd", 1);

        assertEquals(1, order.getQuantityProduct("abcd"),
            "The product shouldn't be in the order when remove all quantity");
    }

    @Test
    void testRemoveProductCalculatePriceAfterRemove() throws InactiveProductException, ProductNotInOrderException {
        when(repository.getProduct("abcd")).thenReturn(new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL));
        when(repository.isProductActive("abcd")).thenReturn(true);

        order.addProduct("abcd", 2);
        order.removeProduct("abcd", 1);

        assertEquals(8.0, order.getTotalPrice(),
            "The price of the order is not calculated correctly");
    }

    @Test
    void testCalculatePriceOfReadyOrder() {
        when(repository.getActiveProduct("abcd")).thenReturn(new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL));
        when(repository.getActiveProduct("efgh")).thenReturn(new Pizza(PizzaType.BBQ_CHICKEN, PizzaSize.SMALL));

        order = new Order(Map.ofEntries(
            Map.entry("abcd", 2),
            Map.entry("efgh", 1)
        ), "test");

        assertEquals(27.0, order.getTotalPrice(),
            "The price of the ready order is incorrect");
    }

    @Test
    void testHashCode() {
        Order order2 = new Order("test2");

        assertNotEquals(order.hashCode(), order2.hashCode(),
            "The hash of the order is not working correctly");
    }

    @Test
    void testEquals() {
        Order order2 = new Order();

        assertNotEquals(order, order2,
            "The hash of the order is not working correctly");
    }

    @AfterEach
    void tearDown() {
        mockedSingleton.close();
    }

}
