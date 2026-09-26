package com.restaurant;

import com.restaurant.enums.Unit;
import com.restaurant.model.Address;
import com.restaurant.model.Ingredient;
import com.restaurant.model.RecipeIngredient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Value Objects & Unit Dimension Compatibility Test Suite")
class AddressAndValueObjectTest {

    @Test
    @DisplayName("Should validate Address record attributes and formatted output")
    void testAddressRecord() {
        Address addr = new Address("42 Commercial Street", "Bangalore", "Karnataka", "560001", "India");
        assertEquals("42 Commercial Street", addr.street());
        assertEquals("Bangalore", addr.city());
        assertEquals("Karnataka", addr.state());
        assertEquals("560001", addr.postalCode());
        assertEquals("India", addr.country());
        assertEquals("42 Commercial Street, Bangalore, Karnataka - 560001", addr.getFormattedAddress());

        assertThrows(NullPointerException.class, () -> new Address(null, "City", "State", "123", "India"));
        assertThrows(NullPointerException.class, () -> new Address("Street", null, "State", "123", "India"));
    }

    @Test
    @DisplayName("Should accurately evaluate dimensional compatibility across all Unit pairs")
    void testUnitDimensionCompatibility() {
        // Mass units are compatible with each other
        assertTrue(Unit.GRAM.isCompatibleWith(Unit.KILOGRAM));
        assertTrue(Unit.KILOGRAM.isCompatibleWith(Unit.GRAM));

        // Volume units are compatible with each other
        assertTrue(Unit.MILLILITER.isCompatibleWith(Unit.LITER));
        assertTrue(Unit.TEASPOON.isCompatibleWith(Unit.TABLESPOON));
        assertTrue(Unit.TABLESPOON.isCompatibleWith(Unit.CUP));

        // Discrete units are compatible with each other
        assertTrue(Unit.PIECE.isCompatibleWith(Unit.PINCH));

        // Incompatible across dimensions
        assertFalse(Unit.GRAM.isCompatibleWith(Unit.MILLILITER));
        assertFalse(Unit.KILOGRAM.isCompatibleWith(Unit.LITER));
        assertFalse(Unit.TABLESPOON.isCompatibleWith(Unit.PIECE));
    }

    @Test
    @DisplayName("Should reject non-positive amounts on RecipeIngredient")
    void testRecipeIngredientInvariants() {
        Ingredient flour = new Ingredient("I-F", "Flour", Unit.GRAM, 1000);

        assertThrows(IllegalArgumentException.class, () -> new RecipeIngredient(flour, 0.0, Unit.GRAM));
        assertThrows(IllegalArgumentException.class, () -> new RecipeIngredient(flour, -5.0, Unit.GRAM));
    }
}

