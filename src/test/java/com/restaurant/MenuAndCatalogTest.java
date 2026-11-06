package com.restaurant;

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
import com.restaurant.model.RecipeIngredient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Menu, Course & Decoupled Pricing Catalog Test Suite")
class MenuAndCatalogTest {

    @Test
    @DisplayName("Should decouple dish from price, allowing multiple menus with different pricing")
    void testDecoupledPricing() {
        Cuisine italian = new Cuisine("CUIS-01", "Italian", CuisineType.ITALIAN);
        Ingredient tomatoes = new Ingredient("ING-10", "Tomatoes", Unit.GRAM, 10000.0);
        Recipe soupRecipe = new Recipe("REC-01", "Tomato Soup", List.of("Simmer tomatoes", "Puree"),
                10, 15, List.of(new RecipeIngredient(tomatoes, 250.0, Unit.GRAM)));

        Dish tomatoSoup = new Dish("DISH-01", "Classic Tomato Soup", "Slow roasted tomatoes with basil",
                DietaryType.VEGAN, italian, soupRecipe);

        // Standard Dine-in Menu sells at 250.00
        MenuItem standardItem = new MenuItem("MI-01", tomatoSoup, BigDecimal.valueOf(250.00));
        assertEquals(BigDecimal.valueOf(250.00), standardItem.getPrice());

        // Airport Express Menu sells the exact same dish at 380.00
        MenuItem airportItem = new MenuItem("MI-02", tomatoSoup, BigDecimal.valueOf(380.00));
        assertEquals(BigDecimal.valueOf(380.00), airportItem.getPrice());

        // Master dish remains untouched and price-agnostic
        assertEquals("Classic Tomato Soup", tomatoSoup.getName());
    }

    @Test
    @DisplayName("Should organize dishes by Course and filter by dietary type")
    void testMenuCourseOrganizationAndDietaryFilter() {
        Cuisine indian = new Cuisine("CUIS-02", "North Indian", CuisineType.INDIAN);
        Ingredient dal = new Ingredient("ING-11", "Black Lentils", Unit.GRAM, 5000.0);
        Ingredient chicken = new Ingredient("ING-12", "Chicken Thigh", Unit.GRAM, 5000.0);

        Dish dalMakhani = new Dish("DISH-02", "Dal Makhani", "Slow cooked lentils", DietaryType.VEG, indian,
                new Recipe("REC-02", "Dal", List.of("Cook"), 10, 30, List.of(new RecipeIngredient(dal, 200, Unit.GRAM))));

        Dish butterChicken = new Dish("DISH-03", "Butter Chicken", "Tandoori chicken in makhani gravy", DietaryType.NON_VEG, indian,
                new Recipe("REC-03", "Butter Chicken", List.of("Cook"), 15, 25, List.of(new RecipeIngredient(chicken, 300, Unit.GRAM))));

        Course mains = new Course("CRS-01", "Main Course");
        MenuCourse mainsSection = new MenuCourse("MC-01", mains, 1);
        mainsSection.addItem(new MenuItem("MI-10", dalMakhani, BigDecimal.valueOf(350.00)));
        mainsSection.addItem(new MenuItem("MI-11", butterChicken, BigDecimal.valueOf(450.00)));

        Menu dinnerMenu = new Menu("MENU-01", "Dinner Menu");
        dinnerMenu.addMenuCourse(mainsSection);

        List<MenuItem> vegItems = dinnerMenu.filterByDietary(DietaryType.VEG);
        assertEquals(1, vegItems.size());
        assertEquals("Dal Makhani", vegItems.getFirst().getDish().getName());

        List<MenuItem> nonVegItems = dinnerMenu.filterByDietary(DietaryType.NON_VEG);
        assertEquals(1, nonVegItems.size());
        assertEquals("Butter Chicken", nonVegItems.getFirst().getDish().getName());
    }
}

