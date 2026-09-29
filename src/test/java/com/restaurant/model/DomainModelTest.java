package com.restaurant.model;

import com.restaurant.enums.DietaryType;
import com.restaurant.enums.IngredientType;
import com.restaurant.enums.MeasurementUnit;
import com.restaurant.exception.InsufficientInventoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Model Unit Tests")
class DomainModelTest {

    @Test
    @DisplayName("Should enforce solid ingredients to be measured in GRAMS")
    void testSolidIngredientMeasurement() {
        Ingredient flour = new Ingredient("ING-1", "Flour", IngredientType.SOLID, MeasurementUnit.GRAMS, 5000);
        assertEquals(MeasurementUnit.GRAMS, flour.getUnit());
        assertEquals(IngredientType.SOLID, flour.getType());
        assertEquals(5000, flour.getCurrentStock());

        assertThrows(IllegalArgumentException.class, () ->
                new Ingredient("ING-ERR", "Salt", IngredientType.SOLID, MeasurementUnit.MILLILITERS, 100));
    }

    @Test
    @DisplayName("Should enforce liquid ingredients to be measured in MILLILITERS")
    void testLiquidIngredientMeasurement() {
        Ingredient oliveOil = new Ingredient("ING-2", "Olive Oil", IngredientType.LIQUID, MeasurementUnit.MILLILITERS, 2000);
        assertEquals(MeasurementUnit.MILLILITERS, oliveOil.getUnit());
        assertEquals(IngredientType.LIQUID, oliveOil.getType());
        assertEquals(2000, oliveOil.getCurrentStock());

        assertThrows(IllegalArgumentException.class, () ->
                new Ingredient("ING-ERR", "Water", IngredientType.LIQUID, MeasurementUnit.GRAMS, 500));
    }

    @Test
    @DisplayName("Should deduct and restock ingredient inventory correctly")
    void testIngredientStockDeductionAndRestock() {
        Ingredient cheese = new Ingredient("ING-3", "Mozzarella", IngredientType.SOLID, MeasurementUnit.GRAMS, 1000);
        cheese.deductStock(250);
        assertEquals(750, cheese.getCurrentStock());

        cheese.addStock(500);
        assertEquals(1250, cheese.getCurrentStock());

        assertThrows(InsufficientInventoryException.class, () -> cheese.deductStock(2000));
        assertThrows(IllegalArgumentException.class, () -> cheese.addStock(-10));
        assertThrows(IllegalArgumentException.class, () -> cheese.deductStock(-10));
    }

    @Test
    @DisplayName("Should throw when initializing ingredient with negative initial stock")
    void testNegativeInitialStock() {
        assertThrows(IllegalArgumentException.class, () ->
                new Ingredient("ING-NEG", "Sugar", IngredientType.SOLID, MeasurementUnit.GRAMS, -50));
    }

    @Test
    @DisplayName("Should reject mismatched units in RecipeIngredient")
    void testRecipeIngredientUnitMismatch() {
        Ingredient milk = new Ingredient("ING-MILK", "Milk", IngredientType.LIQUID, MeasurementUnit.MILLILITERS, 1000);
        assertThrows(IllegalArgumentException.class, () ->
                new RecipeIngredient(milk, 250, MeasurementUnit.GRAMS));
        assertThrows(IllegalArgumentException.class, () ->
                new RecipeIngredient(milk, -5, MeasurementUnit.MILLILITERS));
    }

    @Test
    @DisplayName("Should build Menu with MenuSections and Dishes accurately")
    void testMenuSectionAndDishStructure() {
        Ingredient tomato = new Ingredient("ING-TOM", "Tomato", IngredientType.SOLID, MeasurementUnit.GRAMS, 5000);
        RecipeIngredient ri = new RecipeIngredient(tomato, 200, MeasurementUnit.GRAMS);
        Recipe recipe = new Recipe("REC-SOUP", List.of("Boil tomatoes", "Puree"), 15, List.of(ri));

        Dish tomatoSoup = new Dish("DISH-1", "Tomato Basil Soup", "Classic warm soup", DietaryType.VEGAN, 250.0, recipe);
        assertEquals("Tomato Basil Soup", tomatoSoup.getName());
        assertEquals(250.0, tomatoSoup.getPrice());
        assertEquals(DietaryType.VEGAN, tomatoSoup.getType());

        MenuSection soupsSection = new MenuSection("Soups");
        soupsSection.addDish(tomatoSoup);
        assertEquals(1, soupsSection.getDishes().size());

        Menu menu = new Menu("MENU-1", "Dinner Menu");
        menu.addSection(soupsSection);

        assertTrue(menu.findDishByName("Tomato Basil Soup").isPresent());
        assertTrue(menu.findDishById("DISH-1").isPresent());
        assertFalse(menu.findDishByName("Non Existent").isPresent());
    }

    @Test
    @DisplayName("Should reject negative dish prices and null mandatory fields")
    void testDishValidation() {
        Recipe recipe = new Recipe("R-0", List.of("Simple"), 5, List.of());
        assertThrows(IllegalArgumentException.class, () ->
                new Dish("D-NEG", "Bad Price Dish", "Desc", DietaryType.VEG, -100.0, recipe));
        assertThrows(NullPointerException.class, () ->
                new Dish(null, "No ID", "Desc", DietaryType.VEG, 100.0, recipe));
    }

    @Test
    @DisplayName("Should manage Table capacity and occupancy states")
    void testTableOccupancy() {
        Table table = new Table("T-12", 4);
        assertEquals("T-12", table.getTableNumber());
        assertEquals(4, table.getCapacity());
        assertFalse(table.isOccupied());

        table.markOccupied();
        assertTrue(table.isOccupied());

        table.markAvailable();
        assertFalse(table.isOccupied());

        assertThrows(IllegalArgumentException.class, () -> new Table("T-ERR", 0));
        assertThrows(IllegalArgumentException.class, () -> new Table("T-ERR", -2));
    }

    @Test
    @DisplayName("Should validate Address formatting and equality")
    void testAddressFormatting() {
        Address addr1 = new Address("100 Indiranagar 100ft Rd", "Bengaluru", "Karnataka", "560038");
        Address addr2 = new Address("100 Indiranagar 100ft Rd", "Bengaluru", "Karnataka", "560038");

        assertEquals(addr1, addr2);
        assertEquals(addr1.hashCode(), addr2.hashCode());
        assertEquals("100 Indiranagar 100ft Rd, Bengaluru, Karnataka - 560038", addr1.getFormattedAddress());
    }

    @Test
    @DisplayName("Should validate Customer identity and equality")
    void testCustomerIdentity() {
        Customer c1 = new Customer("C-1", "Rahul", "9876543210", "r@test.com");
        Customer c2 = new Customer("C-1", "Rahul Sharma", "9876543210", "r@test.com");
        Customer c3 = new Customer("C-2", "Aman", "9876543211", "a@test.com");

        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertEquals(c1.hashCode(), c2.hashCode());
    }
}
