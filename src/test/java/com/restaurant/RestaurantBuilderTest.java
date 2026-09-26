package com.restaurant;

import com.restaurant.builder.MenuBuilder;
import com.restaurant.builder.RecipeBuilder;
import com.restaurant.builder.RestaurantBuilder;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.Department;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.Permission;
import com.restaurant.enums.Unit;
import com.restaurant.model.Chef;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.Menu;
import com.restaurant.model.Recipe;
import com.restaurant.model.Role;
import com.restaurant.model.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Fluent Builders Test Suite (Restaurant, Menu, Recipe)")
class RestaurantBuilderTest {

    @Test
    @DisplayName("Should assemble complete Restaurant via RestaurantBuilder")
    void testRestaurantBuilderAssembly() {
        Menu menu = MenuBuilder.create("Lunch Menu").build();
        Cuisine italian = new Cuisine("C-IT", "Italian", CuisineType.ITALIAN);

        Role waiterRole = new Role("R-W", "Server", Department.SERVICE, Set.of(Permission.TAKE_ORDER));
        Waiter waiter = new Waiter("W-1", "Aman", "aman@test.com", "1234567890", waiterRole);

        Role chefRole = new Role("R-C", "Chef", Department.KITCHEN, Set.of(Permission.PREPARE_FOOD));
        Chef chef = new Chef("C-1", "Kunal", "kunal@test.com", "1234567891", chefRole);

        Restaurant restaurant = RestaurantBuilder.create()
                .name("Osteria Bangalore")
                .address("MG Road", "Bangalore", "Karnataka", "560001", "India")
                .menu(menu)
                .addCuisine(italian)
                .addWaiter(waiter)
                .addChef(chef)
                .build();

        assertEquals("Osteria Bangalore", restaurant.getName());
        assertEquals("MG Road, Bangalore, Karnataka - 560001", restaurant.getAddress().getFormattedAddress());
        assertEquals(1, restaurant.getCuisines().size());
        assertEquals(1, restaurant.getWaiters().size());
        assertEquals(1, restaurant.getChefs().size());
    }

    @Test
    @DisplayName("Should build multi-course Menu fluently with dishes and pricing via MenuBuilder")
    void testMenuBuilderAssembly() {
        Cuisine indian = new Cuisine("C-IN", "Indian", CuisineType.INDIAN);
        Recipe dummyRecipe = RecipeBuilder.create("Simple Tadka").addInstruction("Cook dal").build();

        Dish samosa = new Dish("D-SAM", "Samosa", "Crispy pastry", DietaryType.VEGAN, indian, dummyRecipe);
        Dish paneerTikka = new Dish("D-TIK", "Paneer Tikka", "Clay oven roasted", DietaryType.VEG, indian, dummyRecipe);
        Dish biryani = new Dish("D-BIR", "Dum Biryani", "Hyderabadi style", DietaryType.NON_VEG, indian, dummyRecipe);

        Menu menu = MenuBuilder.create("Royal Indian Feast")
                .addCourse("Appetizers", 1)
                    .addDish(samosa, 120.00)
                    .addDish(paneerTikka, 320.00)
                    .and()
                .addCourse("Main Course", 2)
                    .addDish(biryani, 480.00)
                    .and()
                .build();

        assertEquals("Royal Indian Feast", menu.getName());
        assertEquals(2, menu.getMenuCourses().size());
        assertEquals(2, menu.getMenuCourses().get(0).getItems().size());
        assertEquals(1, menu.getMenuCourses().get(1).getItems().size());
    }

    @Test
    @DisplayName("Should build Recipe with instructions, timing, and ingredients via RecipeBuilder")
    void testRecipeBuilderAssembly() {
        Ingredient rice = new Ingredient("ING-RICE", "Basmati Rice", Unit.GRAM, 10000.0);
        Ingredient saffron = new Ingredient("ING-SAF", "Kashmiri Saffron", Unit.PINCH, 100.0);

        Recipe recipe = RecipeBuilder.create("Kashmiri Pulao")
                .prepTime(15)
                .cookTime(20)
                .addInstruction("Wash and soak rice for 30 minutes")
                .addInstruction("Infuse saffron in warm milk")
                .addInstruction("Simmer rice until fragrant")
                .addIngredient(rice, 300.0, Unit.GRAM)
                .addIngredient(saffron, 2.0, Unit.PINCH)
                .build();

        assertEquals("Kashmiri Pulao", recipe.getTitle());
        assertEquals(15, recipe.getPrepTimeMinutes());
        assertEquals(20, recipe.getCookTimeMinutes());
        assertEquals(35, recipe.getTotalTimeMinutes());
        assertEquals(3, recipe.getInstructions().size());
        assertEquals(2, recipe.getIngredients().size());
    }

    @Test
    @DisplayName("Should throw NullPointerException if required builder fields are omitted")
    void testBuilderValidation() {
        assertThrows(NullPointerException.class, () ->
                RestaurantBuilder.create()
                        .name("Incomplete Restaurant")
                        // missing address and menu
                        .build());
    }
}

