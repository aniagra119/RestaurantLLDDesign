package com.restaurant.model;

import com.restaurant.enums.DietaryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Composite Pattern Menu Hierarchy Tests")
class MenuCompositeTest {

    @Test
    @DisplayName("Should treat Menu and MenuSection uniformly via MenuComponent interface")
    void testCompositeMenuHierarchy() {
        Recipe recipe = new Recipe("R-1", List.of("Cook"), 10, List.of());
        Dish soup = new Dish("D-SOUP", "Minestrone", "Classic Italian vegetable soup", DietaryType.VEGAN, 280.0, recipe);
        Dish pasta = new Dish("D-PASTA", "Penne Pesto", "Basil pesto pasta", DietaryType.VEG, 420.0, recipe);

        MenuSection soupsSection = new MenuSection("Starters & Soups");
        soupsSection.addDish(soup);

        MenuSection pastaSection = new MenuSection("Handcrafted Pastas");
        pastaSection.addDish(pasta);

        Menu grandMenu = new Menu("MENU-GRAND", "Grand Italian Dinner Menu");
        grandMenu.addSection(soupsSection);
        grandMenu.addSection(pastaSection);

        // Uniform MenuComponent traversal
        MenuComponent component = grandMenu;
        assertEquals("Grand Italian Dinner Menu", component.getName());
        assertEquals(2, component.getAllDishes().size());
        assertTrue(component.getAllDishes().contains(soup));
        assertTrue(component.getAllDishes().contains(pasta));

        // Sub-component MenuSection traversal
        MenuComponent sectionComponent = soupsSection;
        assertEquals("Starters & Soups", sectionComponent.getName());
        assertEquals(1, sectionComponent.getAllDishes().size());
        assertEquals("Minestrone", sectionComponent.getAllDishes().get(0).getName());

        // Verify display executes cleanly
        assertDoesNotThrow(() -> component.display(0));
    }
}
