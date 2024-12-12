package com.deliciouspizza.entity.product;

import com.deliciouspizza.enums.SauceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SauceTest {

    private Sauce sauce;

    @Test
    void testCalculatePriceNullValues() {
        sauce = new Sauce();

        assertThrows(IllegalArgumentException.class, () -> sauce.calculatePrice(),
            "An exception should be thrown when calculating the prize of sauce with null sauce");
    }

    @Test
    void testCalculatePriceTomatoSauce() {
        sauce = new Sauce(SauceType.TOMATO_SAUCE);

        assertEquals(1.0, sauce.calculatePrice(),
            "Incorrect calculation of the prize of sauce");
    }

    @Test
    void testGenerateKeyNullValues() {
        sauce = new Sauce();

        assertThrows(IllegalArgumentException.class, () -> sauce.generateKey(),
            "An exception should be thrown when generating key with null value");
    }

    @Test
    void testGenerateKeyBBQSauce() {
        sauce = new Sauce(SauceType.BBQ_SAUCE);

        assertEquals(1.50, sauce.calculatePrice(),
            "The prize of the BBQ sauce is not calculated properly");
    }

    @Test
    void testGetFormattedDetailsNullValues() {
        sauce = new Sauce();

        assertThrows(IllegalArgumentException.class, () -> sauce.getFormattedDetails(),
            "An exception should be thrown when formatting the details of sauce with null value");
    }

    @Test
    void testGetFormattedDetailsPesto() {
        sauce = new Sauce(SauceType.PESTO);

        assertEquals("PESTO", sauce.getFormattedDetails(),
            "The formatting of the sauce is incorrect");
    }

    @Test
    void testEquals() {
        sauce = new Sauce(SauceType.WHITE_SAUCE);
        Sauce sauce2 = new Sauce(SauceType.WHITE_SAUCE);

        assertEquals(sauce, sauce2, "Incorrect implementation of equals() for the sauce");
    }

    @Test
    void testNotEquals() {
        sauce = new Sauce(SauceType.WHITE_SAUCE);
        Sauce sauce2 = new Sauce(SauceType.SPICY_SAUCE);

        assertNotEquals(sauce, sauce2, "Incorrect implementation of equals() for the sauce");
    }

    @Test
    void testHashCodeSame() {
        sauce = new Sauce(SauceType.RANCH);
        Sauce sauce2 = new Sauce(SauceType.RANCH);

        assertEquals(sauce.hashCode(), sauce2.hashCode(), "The hashCode() is not working properly for the sauce");
    }

    @Test
    void testHashCodeDifferent() {
        sauce = new Sauce(SauceType.HONEY_MUSTARD);
        Sauce sauce2 = new Sauce(SauceType.WHITE_SAUCE);

        assertNotEquals(sauce.hashCode(), sauce2.hashCode(), "The hashCode() is not working properly for the sauce");
    }
}
