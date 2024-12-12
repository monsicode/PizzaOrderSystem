package com.deliciouspizza.entity.product;

import com.deliciouspizza.enums.PizzaSize;
import com.deliciouspizza.enums.PizzaType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PizzaTest {

    private Pizza pizza;

    @Test
    void testCalculatePrizeNullValues() {
        pizza = new Pizza();

        assertThrows(IllegalArgumentException.class, () -> pizza.calculatePrice(),
            "An exception should be thrown when we calculate the prize of pizza with null values");
    }

    @Test
    void testCalculatePrizeSmallPepperoni() {
        pizza = new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL);

        assertEquals(8.0, pizza.calculatePrice(),
            "The prize of small margherita is calculated incorrectly");
    }

    @Test
    void testCalculatePrizeLargeVegan() {
        pizza = new Pizza(PizzaType.VEGGIE, PizzaSize.LARGE);

        assertEquals(14.0, pizza.calculatePrice(),
            "The prize of large veggie pizza is calculated incorrectly");
    }

    @Test
    void testGenerateKeyNullPizzaType() {
        assertThrows(IllegalArgumentException.class, () -> new Pizza(null, PizzaSize.LARGE),
            "An IllegalArgumentException should be thrown when we generate kye with null pizza type");
    }

    @Test
    void testGenerateKeyNullPizzaSize() {
        assertThrows(IllegalArgumentException.class, () -> new Pizza(PizzaType.VEGGIE, null),
            "An IllegalArgumentException should be thrown when we generate kye with null pizza type");
    }

    @Test
    void testGenerateKeyNullValues() {
        pizza = new Pizza();

        assertThrows(IllegalArgumentException.class, () -> pizza.generateKey(),
            "An exception should be thrown when we generate key of pizza with null values");
    }

    @Test
    void testGenerateKeySmallPepperoni() {
        pizza = new Pizza(PizzaType.PEPPERONI, PizzaSize.SMALL);

        assertEquals("pizza_pepperoni_small", pizza.generateKey(),
            "Incorrect generation of the key of small pepperoni");
    }

    @Test
    void testGenerateKeyMediumHawaiian() {
        pizza = new Pizza(PizzaType.HAWAIIAN, PizzaSize.MEDIUM);

        assertEquals("pizza_hawaiian_medium", pizza.generateKey(),
            "Incorrect generation of the key of medium hawaiian");
    }

    @Test
    void testGenerateKeyLargeSeafood() {
        pizza = new Pizza(PizzaType.SEAFOOD, PizzaSize.LARGE);

        assertEquals("pizza_seafood_large", pizza.generateKey(),
            "Incorrect generation of the key of large seafood");
    }

    @Test
    void testGetFormattedDetails() {
        pizza = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);

        assertEquals("Pizza: MARGHERITA, Size: LARGE", pizza.getFormattedDetails(),
            "Incorrect formatting of the details of large pizza");
    }

    @Test
    void testHashCodeSame() {
        pizza = new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL);
        Pizza pizza2 = new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL);

        assertEquals(pizza.hashCode(), pizza2.hashCode(), "The hashCode() is not working properly for the pizza");
    }

    @Test
    void testHashCodeDifferent() {
        pizza = new Pizza(PizzaType.MARGHERITA, PizzaSize.LARGE);
        Pizza pizza2 = new Pizza(PizzaType.MARGHERITA, PizzaSize.SMALL);

        assertNotEquals(pizza.hashCode(), pizza2.hashCode(), "The hashCode() is not working properly for the pizza");
    }
}
