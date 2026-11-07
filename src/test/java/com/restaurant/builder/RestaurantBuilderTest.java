package com.restaurant.builder;

import com.restaurant.Restaurant;
import com.restaurant.enums.ChefLevel;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.IngredientType;
import com.restaurant.enums.MeasurementUnit;
import com.restaurant.enums.WaiterLevel;
import com.restaurant.model.Address;
import com.restaurant.model.Chef;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.Menu;
import com.restaurant.model.Recipe;
import com.restaurant.model.Table;
import com.restaurant.model.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Fluent Builders Tests")
class RestaurantBuilderTest {

    @Test
    @DisplayName("Should fluently build Recipe, Menu, and Restaurant instances")
    void testFluentBuilders() {
        Ingredient rice = new Ingredient("ING-RICE", "Basmati Rice", IngredientType.SOLID, MeasurementUnit.GRAMS, 10000);

        Recipe biryaniRecipe = new RecipeBuilder()
                .setId("REC-BIRYANI")
                .addInstruction("Soak rice")
                .addInstruction("Dum cook on slow fire")
                .setPrepTimeMinutes(30)
                .addIngredient(rice, 300, MeasurementUnit.GRAMS)
                .build();

        assertEquals("REC-BIRYANI", biryaniRecipe.getId());
        assertEquals(2, biryaniRecipe.getInstructions().size());

        Dish biryani = new Dish("D-BIR", "Hyderabadi Dum Biryani", "Fragrant rice dish", DietaryType.NON_VEG, 450.0, biryaniRecipe);

        Menu menu = new MenuBuilder()
                .setMenuId("MENU-MAIN")
                .setName("Royal Dining Menu")
                .addDishToSection("Main Course", biryani)
                .build();

        assertEquals(1, menu.getSections().size());
        assertEquals("Main Course", menu.getSections().get(0).getSectionName());

        Restaurant restaurant = new RestaurantBuilder()
                .setName("The Royal Peacock")
                .setAddress(new Address("MG Road", "Bengaluru", "Karnataka", "560001"))
                .addCuisine(CuisineType.INDIAN)
                .setMenu(menu)
                .addWaiter(new Waiter("W-1", "Kunal", "kunal@test.com", "9876543210", WaiterLevel.SENIOR))
                .addChef(new Chef("C-1", "Imtiaz Qureshi", "imtiaz@test.com", "9876543211", ChefLevel.EXECUTIVE_CHEF))
                .addTable(new Table("T-10", 6))
                .build();

        assertNotNull(restaurant);
        assertEquals("The Royal Peacock", restaurant.getName());
        assertEquals(1, restaurant.getCuisines().size());
        assertEquals(1, restaurant.getWaiters().size());
        assertEquals(1, restaurant.getChefs().size());
        assertEquals(1, restaurant.getTables().size());
    }

    @Test
    @DisplayName("Should reject incomplete restaurant builds with missing mandatory fields")
    void testIncompleteRestaurantBuild() {
        RestaurantBuilder builder = new RestaurantBuilder();
        assertThrows(NullPointerException.class, builder::build);

        builder.setName("Incomplete Bistro");
        assertThrows(NullPointerException.class, builder::build);

        builder.setAddress(new Address("Street", "City", "State", "000000"));
        assertThrows(NullPointerException.class, builder::build);
    }
}
