package com.restaurant.customization;

import com.restaurant.enums.DietaryType;
import com.restaurant.model.Dish;
import com.restaurant.model.Recipe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dish Customization Decorator Pattern Tests")
class DishCustomizationTest {

    private Dish createBasePasta() {
        Recipe pastaRecipe = new Recipe("REC-PST", List.of("Boil pasta", "Toss in garlic butter"), 15, List.of());
        return new Dish("DISH-PST", "Aglio e Olio", "Garlic and olive oil pasta", DietaryType.VEGAN, 350.0, pastaRecipe);
    }

    @Test
    @DisplayName("Should dynamically customize base dish with multiple decorators")
    void testDecoratorStacking() {
        Dish pasta = createBasePasta();

        OrderableItem item = new BaseDishItem(pasta);
        assertEquals(350.0, item.getPrice());
        assertEquals("Aglio e Olio", item.getName());
        assertEquals(DietaryType.VEGAN, item.getDietaryType());

        // Decorate with extra cheese (+₹50)
        item = new ExtraToppingDecorator(item, "Parmesan Shavings", 50.0);
        assertEquals(400.0, item.getPrice());
        assertEquals("Aglio e Olio + Parmesan Shavings", item.getName());

        // Decorate with spice level
        item = new SpiceLevelDecorator(item, "Extra Spicy");
        assertEquals(400.0, item.getPrice());
        assertEquals("Aglio e Olio + Parmesan Shavings (Extra Spicy)", item.getName());

        // Decorate with Grilled Chicken (+₹120) -> Switches to NON_VEG
        item = new ProteinDecorator(item, "Grilled Chicken", 120.0);
        assertEquals(520.0, item.getPrice());
        assertEquals("Aglio e Olio + Parmesan Shavings (Extra Spicy) with Grilled Chicken", item.getName());
        assertEquals(DietaryType.NON_VEG, item.getDietaryType());
    }

    @Test
    @DisplayName("Should support stacking multiple extra toppings")
    void testMultipleToppings() {
        Dish pasta = createBasePasta();
        OrderableItem item = new BaseDishItem(pasta);

        item = new ExtraToppingDecorator(item, "Olives", 40.0);
        item = new ExtraToppingDecorator(item, "Jalapenos", 30.0);
        item = new ExtraToppingDecorator(item, "Sundried Tomatoes", 60.0);

        assertEquals(480.0, item.getPrice()); // 350 + 40 + 30 + 60
        assertTrue(item.getName().contains("Olives"));
        assertTrue(item.getName().contains("Jalapenos"));
        assertTrue(item.getName().contains("Sundried Tomatoes"));
    }

    @Test
    @DisplayName("Should reject negative costs on decorators")
    void testNegativeDecoratorCost() {
        Dish pasta = createBasePasta();
        OrderableItem item = new BaseDishItem(pasta);

        assertThrows(IllegalArgumentException.class, () ->
                new ExtraToppingDecorator(item, "Invalid Topping", -20.0));
        assertThrows(IllegalArgumentException.class, () ->
                new ProteinDecorator(item, "Invalid Protein", -50.0));
    }
}
