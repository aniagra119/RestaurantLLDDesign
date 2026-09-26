package com.restaurant;

import com.restaurant.builder.MenuBuilder;
import com.restaurant.builder.RecipeBuilder;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.Unit;
import com.restaurant.model.Course;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.Menu;
import com.restaurant.model.MenuCourse;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MenuCourse Lifecycle, Item Availability & Lookup Test Suite")
class MenuCourseAndItemManagementTest {

    private Menu menu;
    private MenuCourse breakfastCourse;
    private MenuItem dosaItem;
    private MenuItem idliItem;

    @BeforeEach
    void setUp() {
        Cuisine southIndian = new Cuisine("C-SI", "South Indian", CuisineType.INDIAN);
        Recipe dummyRecipe = RecipeBuilder.create("Batter Recipe").build();

        Dish masalaDosa = new Dish("D-DOSA", "Masala Dosa", "Crispy crepe with spiced potato", DietaryType.VEGAN, southIndian, dummyRecipe);
        Dish idliVada = new Dish("D-IDLI", "Idli Vada", "Steamed rice cakes with lentil doughnut", DietaryType.VEGAN, southIndian, dummyRecipe);

        dosaItem = new MenuItem("MI-DOSA", masalaDosa, BigDecimal.valueOf(110.00));
        idliItem = new MenuItem("MI-IDLI", idliVada, BigDecimal.valueOf(80.00));

        Course breakfast = new Course("CRS-BRK", "Morning Tiffin");
        breakfastCourse = new MenuCourse("MC-BRK", breakfast, 1);
        breakfastCourse.addItem(dosaItem);
        breakfastCourse.addItem(idliItem);

        menu = new Menu("MENU-TIFFIN", "Breakfast & Tiffin Menu");
        menu.addMenuCourse(breakfastCourse);
    }

    @Test
    @DisplayName("Should omit all items when entire MenuCourse is deactivated (e.g. 11 AM breakfast cutoff)")
    void testMenuCourseDeactivationCutoff() {
        // Active course returns 2 items
        assertEquals(2, menu.filterByDietary(DietaryType.VEGAN).size());

        // 11:00 AM arrives -> manager turns off breakfast course
        breakfastCourse.setActive(false);

        // Filter should now return 0 items because the section is inactive
        assertEquals(0, menu.filterByDietary(DietaryType.VEGAN).size());
    }

    @Test
    @DisplayName("Should omit item when kitchen marks specific MenuItem out of stock")
    void testMenuItemOutOfStockToggle() {
        assertEquals(2, menu.filterByDietary(DietaryType.VEGAN).size());

        // Kitchen runs out of dosa batter
        dosaItem.setAvailable(false);
        assertFalse(dosaItem.isAvailable());

        List<MenuItem> availableVegans = menu.filterByDietary(DietaryType.VEGAN);
        assertEquals(1, availableVegans.size());
        assertEquals("Idli Vada", availableVegans.getFirst().getDish().getName());
    }

    @Test
    @DisplayName("Should find MenuItem by ID or return empty Optional for non-existent ID")
    void testMenuItemLookup() {
        Optional<MenuItem> found = menu.findMenuItemById("MI-DOSA");
        assertTrue(found.isPresent());
        assertEquals("Masala Dosa", found.get().getDish().getName());

        Optional<MenuItem> missing = menu.findMenuItemById("MI-NONEXISTENT");
        assertTrue(missing.isEmpty());
    }

    @Test
    @DisplayName("Should remove item from MenuCourse")
    void testRemoveItemFromCourse() {
        assertEquals(2, breakfastCourse.getItems().size());
        breakfastCourse.removeItem("MI-DOSA");
        assertEquals(1, breakfastCourse.getItems().size());
        assertEquals("MI-IDLI", breakfastCourse.getItems().getFirst().getId());
    }
}

