package com.deliciouspizza.entity.product;

import com.deliciouspizza.enums.DrinkType;
import com.deliciouspizza.enums.DrinkVolume;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DrinkTest {

    private Drink drink;

    @Test
    void testCalculatePriceNullValues() {
        drink = new Drink();

        assertThrows(IllegalArgumentException.class, () -> drink.calculatePrice(),
            "The calculation of price should throw an excpetion of any of the values are null");
    }

    @Test
    void testCalculatePriceLargeBeer() {
        drink = new Drink(DrinkType.BEER, DrinkVolume.LARGE);

        assertEquals(6.0, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceSmallBeer() {
        drink = new Drink(DrinkType.BEER, DrinkVolume.SMALL);

        assertEquals(4.0, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceLargeCoke() {
        drink = new Drink(DrinkType.COKE, DrinkVolume.LARGE);

        assertEquals(5.50, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceSmallCoke() {
        drink = new Drink(DrinkType.COKE, DrinkVolume.SMALL);

        assertEquals(3.50, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceSmallPepsi() {
        drink = new Drink(DrinkType.PEPSI, DrinkVolume.SMALL);

        assertEquals(3.40, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceLargePepsi() {
        drink = new Drink(DrinkType.PEPSI, DrinkVolume.LARGE);

        assertEquals(5.40, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculateSmallWater() {
        drink = new Drink(DrinkType.WATER, DrinkVolume.SMALL);

        assertEquals(2.50, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceLargeWater() {
        drink = new Drink(DrinkType.WATER, DrinkVolume.LARGE);

        assertEquals(4.50, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceSmallOrangeJuice() {
        drink = new Drink(DrinkType.ORANGE_JUICE, DrinkVolume.SMALL);

        assertEquals(4.0, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceLargeOrangeJuice() {
        drink = new Drink(DrinkType.ORANGE_JUICE, DrinkVolume.LARGE);

        assertEquals(6.0, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceSmallAppleJuice() {
        drink = new Drink(DrinkType.APPLE_JUICE, DrinkVolume.SMALL);

        assertEquals(4.0, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceLargeAppleJuice() {
        drink = new Drink(DrinkType.APPLE_JUICE, DrinkVolume.LARGE);

        assertEquals(6.0, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceSmallFanta() {
        drink = new Drink(DrinkType.FANTA, DrinkVolume.SMALL);

        assertEquals(3.50, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceLargeFanta() {
        drink = new Drink(DrinkType.FANTA, DrinkVolume.LARGE);

        assertEquals(5.50, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceSmallSprite() {
        drink = new Drink(DrinkType.SPRITE, DrinkVolume.SMALL);

        assertEquals(3.50, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceLargeSprite() {
        drink = new Drink(DrinkType.SPRITE, DrinkVolume.LARGE);

        assertEquals(5.50, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceSmallLemonade() {
        drink = new Drink(DrinkType.LEMONADE, DrinkVolume.SMALL);

        assertEquals(3.0, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceLargeLemonade() {
        drink = new Drink(DrinkType.LEMONADE, DrinkVolume.LARGE);

        assertEquals(5.0, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceSmallGreenTea() {
        drink = new Drink(DrinkType.GREEN_TEA, DrinkVolume.SMALL);

        assertEquals(3.2, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceLargeGreenTea() {
        drink = new Drink(DrinkType.GREEN_TEA, DrinkVolume.LARGE);

        assertEquals(5.2, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceSmallWine() {
        drink = new Drink(DrinkType.WINE, DrinkVolume.SMALL);

        assertEquals(6.0, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testCalculatePriceLargeWine() {
        drink = new Drink(DrinkType.WINE, DrinkVolume.LARGE);

        assertEquals(8.0, drink.calculatePrice(),
            "The price of the drink is not calculated correctly");
    }

    @Test
    void testGenerateKeyNullValues() {
        drink = new Drink();

        assertThrows(IllegalArgumentException.class, () -> drink.generateKey(),
            "An exception should be thrown when we generate key with null values");
    }

    @Test
    void testGenerateKeySmallBeer() {
        drink = new Drink(DrinkType.BEER, DrinkVolume.SMALL);

        assertEquals("drink_beer_small", drink.generateKey(),
            "Generate key does not create the correct key");
    }

    @Test
    void testGenerateKeyLargeCoke() {
        drink = new Drink(DrinkType.COKE, DrinkVolume.LARGE);

        assertEquals("drink_coke_large", drink.generateKey(),
            "Generate key does not create the correct key");
    }

    @Test
    void testIsAlcoholicCoke() {
        drink = new Drink(DrinkType.COKE, DrinkVolume.SMALL);

        assertFalse(drink.getIsAlcoholic(), "The coke should be non-alcoholic drink");
    }

    @Test
    void testIsAlcoholicPepsi() {
        drink = new Drink(DrinkType.PEPSI, DrinkVolume.SMALL);

        assertFalse(drink.getIsAlcoholic(), "The coke should be non-alcoholic drink");
    }

    @Test
    void testIsAlcoholicWater() {
        drink = new Drink(DrinkType.WATER, DrinkVolume.SMALL);

        assertFalse(drink.getIsAlcoholic(), "The coke should be non-alcoholic drink");
    }

    @Test
    void testIsAlcoholicOrangeJuice() {
        drink = new Drink(DrinkType.ORANGE_JUICE, DrinkVolume.SMALL);

        assertFalse(drink.getIsAlcoholic(), "The coke should be non-alcoholic drink");
    }

    @Test
    void testIsAlcoholicAppleJuice() {
        drink = new Drink(DrinkType.APPLE_JUICE, DrinkVolume.SMALL);

        assertFalse(drink.getIsAlcoholic(), "The coke should be non-alcoholic drink");
    }

    @Test
    void testIsAlcoholicFanta() {
        drink = new Drink(DrinkType.FANTA, DrinkVolume.SMALL);

        assertFalse(drink.getIsAlcoholic(), "The coke should be non-alcoholic drink");
    }

    @Test
    void testIsAlcoholicSprite() {
        drink = new Drink(DrinkType.SPRITE, DrinkVolume.SMALL);

        assertFalse(drink.getIsAlcoholic(), "The coke should be non-alcoholic drink");
    }

    @Test
    void testIsAlcoholicLemonade() {
        drink = new Drink(DrinkType.LEMONADE, DrinkVolume.SMALL);

        assertFalse(drink.getIsAlcoholic(), "The coke should be non-alcoholic drink");
    }

    @Test
    void testIsAlcoholicGreenTea() {
        drink = new Drink(DrinkType.GREEN_TEA, DrinkVolume.SMALL);

        assertFalse(drink.getIsAlcoholic(), "The coke should be non-alcoholic drink");
    }

    @Test
    void testIsAlcoholicBeer() {
        drink = new Drink(DrinkType.BEER, DrinkVolume.SMALL);

        assertTrue(drink.getIsAlcoholic(), "The coke should be alcoholic drink");
    }

    @Test
    void testIsAlcoholicWine() {
        drink = new Drink(DrinkType.WINE, DrinkVolume.SMALL);

        assertTrue(drink.getIsAlcoholic(), "The coke should be alcoholic drink");
    }

    @Test
    void testEqualsSameDrinkBeer() {
        drink = new Drink(DrinkType.BEER, DrinkVolume.LARGE);
        Drink drink1 = new Drink(DrinkType.BEER, DrinkVolume.LARGE);

        assertEquals(drink, drink1, "Drinks of the same type should be equal");
    }

    @Test
    void testEqualsSameTypeDifferentVolume() {
        drink = new Drink(DrinkType.COKE, DrinkVolume.SMALL);
        Drink drink1 = new Drink(DrinkType.COKE, DrinkVolume.LARGE);

        assertNotEquals(drink, drink1, "Drinks of the same type but with different volume shouldn't be equal");
    }

    @Test
    void testEqualsDifferentType() {
        drink = new Drink(DrinkType.COKE, DrinkVolume.LARGE);
        Drink drink1 = new Drink(DrinkType.BEER, DrinkVolume.LARGE);

        assertNotEquals(drink, drink1, "Drinks of different types should not be equal");
    }

    @Test
    void testHashCodeSameType() {
        drink = new Drink(DrinkType.COKE, DrinkVolume.LARGE);
        Drink drink1 = new Drink(DrinkType.COKE, DrinkVolume.LARGE);

        assertEquals(drink.hashCode(), drink1.hashCode(),
            "Two drinks of the same type should have the same hash");
    }

    @Test
    void testHashCodeDifferentType() {
        drink = new Drink(DrinkType.COKE, DrinkVolume.SMALL);
        Drink drink1 = new Drink(DrinkType.BEER, DrinkVolume.SMALL);

        assertNotEquals(drink.hashCode(), drink1.hashCode(),
            "Two drinks of the different type shouldn't have the same hash");
    }

    @Test
    void testIsGoodForUnderAgeCustomersCoke() {
        drink = new Drink(DrinkType.COKE, DrinkVolume.LARGE);

        assertTrue(drink.isItGoodForUnderAgedCustomers(),
            "Non-alcoholic drink like COKE should be good for customers under age of 18");
    }

    @Test
    void testIsGoodForUnderAgeCustomersBeer() {
        drink = new Drink(DrinkType.BEER, DrinkVolume.LARGE);

        assertFalse(drink.isItGoodForUnderAgedCustomers(),
            "Alcoholic drink like COKE should not be good for customers under age of 18");
    }

    @Test
    void testGetFormattedDetails() {
        drink = new Drink(DrinkType.BEER, DrinkVolume.LARGE);

        assertEquals("Drink: BEER, Volume: LARGE", drink.getFormattedDetails(),
            "The details are not formatted correctly");
    }
}

