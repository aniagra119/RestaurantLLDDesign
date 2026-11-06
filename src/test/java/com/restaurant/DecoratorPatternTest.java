package com.restaurant;

import com.restaurant.decorator.AddOnDecorator;
import com.restaurant.decorator.BaseDishItem;
import com.restaurant.decorator.PreparedItem;
import com.restaurant.decorator.ProteinDecorator;
import com.restaurant.decorator.SauceDecorator;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.Unit;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Decorator Pattern Customization & Recipe Assembly Test Suite")
class DecoratorPatternTest {

    @Test
    @DisplayName("Should dynamically decorate base dish with protein, sauce, and extra add-ons")
    void testDecoratorComposition() {
        Cuisine italian = new Cuisine("CUIS-03", "Italian", CuisineType.ITALIAN);

        Ingredient penne = new Ingredient("ING-20", "Penne Pasta", Unit.GRAM, 5000.0);
        Ingredient chicken = new Ingredient("ING-21", "Chicken Breast", Unit.GRAM, 5000.0);
        Ingredient alfredo = new Ingredient("ING-22", "Alfredo Cream Sauce", Unit.MILLILITER, 5000.0);
        Ingredient parmesan = new Ingredient("ING-23", "Parmesan Cheese", Unit.GRAM, 2000.0);

        Recipe baseRecipe = new Recipe("REC-20", "Base Penne",
                List.of("Boil pasta in salted water for 9 minutes"),
                5, 9, List.of(new RecipeIngredient(penne, 150.0, Unit.GRAM)));

        Dish basePasta = new Dish("DISH-20", "Penne", "Classic artisan pasta", DietaryType.VEGAN, italian, baseRecipe);
        MenuItem pastaMenuItem = new MenuItem("MI-20", basePasta, BigDecimal.valueOf(300.00));

        // Step 1: Base component
        PreparedItem item = new BaseDishItem(pastaMenuItem);
        assertEquals("Penne", item.getName());
        assertEquals(BigDecimal.valueOf(300.00), item.getPrice());
        assertEquals(DietaryType.VEGAN, item.getDietaryType());
        assertEquals(1, item.getIngredients().size());
        assertEquals(basePasta, item.getRootDish());

        // Step 2: Decorate with Chicken (Non-Veg protein)
        RecipeIngredient chickenPortion = new RecipeIngredient(chicken, 180.0, Unit.GRAM);
        item = new ProteinDecorator(item, "Chicken", BigDecimal.valueOf(100.00), DietaryType.NON_VEG, chickenPortion);

        assertEquals("Penne with Chicken", item.getName());
        assertEquals(BigDecimal.valueOf(400.00), item.getPrice());
        assertEquals(DietaryType.NON_VEG, item.getDietaryType()); // Promoted to NON_VEG!
        assertEquals(2, item.getIngredients().size());
        assertEquals(basePasta, item.getRootDish());

        // Step 3: Decorate with Alfredo Sauce
        RecipeIngredient saucePortion = new RecipeIngredient(alfredo, 100.0, Unit.MILLILITER);
        item = new SauceDecorator(item, "Creamy Alfredo", BigDecimal.valueOf(50.00), saucePortion);

        assertEquals("Penne with Chicken in Creamy Alfredo Sauce", item.getName());
        assertEquals(BigDecimal.valueOf(450.00), item.getPrice());
        assertEquals(3, item.getIngredients().size());

        // Step 4: Decorate with Extra Parmesan Cheese
        RecipeIngredient cheesePortion = new RecipeIngredient(parmesan, 30.0, Unit.GRAM);
        item = new AddOnDecorator(item, "Parmesan", BigDecimal.valueOf(40.00), cheesePortion);

        assertEquals("Penne with Chicken in Creamy Alfredo Sauce (+ Extra Parmesan)", item.getName());
        assertEquals(BigDecimal.valueOf(490.00), item.getPrice());
        assertEquals(4, item.getIngredients().size());
        assertEquals(4, item.getCookingInstructions().size());
    }

    @Test
    @DisplayName("Should maintain vegetarian dietary type when protein decorator is vegetarian")
    void testVegetarianProteinDecoratorPreservesDietaryType() {
        Cuisine italian = new Cuisine("CUIS-04", "Italian", CuisineType.ITALIAN);
        Ingredient tofu = new Ingredient("ING-TOFU", "Organic Tofu", Unit.GRAM, 2000.0);
        Recipe baseRecipe = new Recipe("REC-VEG", "Base Bowl", List.of("Cook grain"), 5, 5, List.of());
        Dish bowl = new Dish("D-BOWL", "Grain Bowl", "Healthy grains", DietaryType.VEGAN, italian, baseRecipe);
        MenuItem bowlItem = new MenuItem("MI-BOWL", bowl, BigDecimal.valueOf(250.00));

        PreparedItem item = new BaseDishItem(bowlItem);
        RecipeIngredient tofuPortion = new RecipeIngredient(tofu, 150.0, Unit.GRAM);
        item = new ProteinDecorator(item, "Tofu", BigDecimal.valueOf(60.00), DietaryType.VEGAN, tofuPortion);

        assertEquals(DietaryType.VEGAN, item.getDietaryType());
        assertEquals(BigDecimal.valueOf(310.00), item.getPrice());
    }

    @Test
    @DisplayName("Should chain multiple AddOnDecorators additively without price or instruction loss")
    void testMultipleAddOnDecoratorsChaining() {
        Cuisine italian = new Cuisine("CUIS-05", "Italian", CuisineType.ITALIAN);
        Dish toast = new Dish("D-TOAST", "Sourdough Toast", "Warm artisan bread", DietaryType.VEGAN, italian,
                new Recipe("R-TST", "Toast", List.of("Toast bread"), 2, 3, List.of()));
        MenuItem toastItem = new MenuItem("MI-TOAST", toast, BigDecimal.valueOf(150.00));

        PreparedItem item = new BaseDishItem(toastItem);
        item = new AddOnDecorator(item, "Avocado", BigDecimal.valueOf(80.00), null);
        item = new AddOnDecorator(item, "Chili Flakes", BigDecimal.valueOf(10.00), null);

        // 150 + 80 + 10 = 240.00
        assertEquals(BigDecimal.valueOf(240.00), item.getPrice());
        assertTrue(item.getName().contains("Extra Avocado"));
        assertTrue(item.getName().contains("Extra Chili Flakes"));
        assertEquals(3, item.getCookingInstructions().size());
    }
}
