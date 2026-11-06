package com.restaurant;

import com.restaurant.enums.Unit;
import com.restaurant.model.Ingredient;
import com.restaurant.model.RecipeIngredient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Measurement, Unit Conversion & Inventory Depletion Test Suite")
class MeasurementAndInventoryTest {

    @Test
    @DisplayName("Should convert tablespoons, teaspoons, and cups to base milliliters")
    void testVolumeUnitConversions() {
        Ingredient oliveOil = new Ingredient("ING-01", "Extra Virgin Olive Oil", Unit.MILLILITER, 1000.0);

        RecipeIngredient tbspOil = new RecipeIngredient(oliveOil, 2.0, Unit.TABLESPOON);
        assertEquals(30.0, tbspOil.getBaseAmount(), 0.001); // 2 * 15 = 30 ml

        RecipeIngredient tspOil = new RecipeIngredient(oliveOil, 3.0, Unit.TEASPOON);
        assertEquals(15.0, tspOil.getBaseAmount(), 0.001); // 3 * 5 = 15 ml

        RecipeIngredient cupOil = new RecipeIngredient(oliveOil, 0.5, Unit.CUP);
        assertEquals(120.0, cupOil.getBaseAmount(), 0.001); // 0.5 * 240 = 120 ml
    }

    @Test
    @DisplayName("Should convert kilograms to base grams")
    void testMassUnitConversions() {
        Ingredient flour = new Ingredient("ING-02", "Wheat Flour", Unit.GRAM, 50000.0);

        RecipeIngredient kgFlour = new RecipeIngredient(flour, 2.5, Unit.KILOGRAM);
        assertEquals(2500.0, kgFlour.getBaseAmount(), 0.001); // 2.5 * 1000 = 2500 grams
    }

    @Test
    @DisplayName("Should throw exception if unit is dimensionally incompatible with ingredient base unit")
    void testIncompatibleUnitsThrowException() {
        Ingredient salt = new Ingredient("ING-03", "Sea Salt", Unit.GRAM, 500.0);

        // Cannot measure solid salt in milliliters without explicit liquid conversion
        assertThrows(IllegalArgumentException.class, () ->
                new RecipeIngredient(salt, 100.0, Unit.MILLILITER));
    }

    @Test
    @DisplayName("Should deduct inventory stock accurately and prevent overdrawing")
    void testInventoryDeduction() {
        Ingredient paneer = new Ingredient("ING-04", "Paneer", Unit.GRAM, 500.0);

        paneer.deductStock(200.0);
        assertEquals(300.0, paneer.getAvailableQuantity(), 0.001);

        paneer.restock(150.0);
        assertEquals(450.0, paneer.getAvailableQuantity(), 0.001);

        assertThrows(IllegalStateException.class, () -> paneer.deductStock(500.0));
    }
}

