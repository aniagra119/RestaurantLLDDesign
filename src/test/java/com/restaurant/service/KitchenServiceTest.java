package com.restaurant.service;

import com.restaurant.customization.BaseDishItem;
import com.restaurant.enums.ChefLevel;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.IngredientType;
import com.restaurant.enums.MeasurementUnit;
import com.restaurant.enums.OrderStatus;
import com.restaurant.event.OrderEventPublisher;
import com.restaurant.exception.InsufficientInventoryException;
import com.restaurant.model.Chef;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kitchen Service & Inventory Deductions Test")
class KitchenServiceTest {

    @Test
    @DisplayName("Should prepare order and deduct recipe inventory accurately")
    void testKitchenPreparationAndInventoryDeduction() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        KitchenService kitchen = new KitchenService(publisher);

        Ingredient flour = new Ingredient("ING-FLOUR", "Flour", IngredientType.SOLID, MeasurementUnit.GRAMS, 1000);
        RecipeIngredient ri = new RecipeIngredient(flour, 200, MeasurementUnit.GRAMS);
        Recipe recipe = new Recipe("REC-PIZZA", List.of("Knead", "Bake"), 15, List.of(ri));
        Dish pizza = new Dish("DISH-PZ", "Margherita", "Pizza", DietaryType.VEG, 400.0, recipe);

        Order order = new Order("ORD-101");
        order.addItem(new OrderItem("IT-1", new BaseDishItem(pizza), 2)); // Needs 400g flour

        Chef chef = new Chef("CHEF-1", "Vikas Khanna", "vikas@test.com", "9900000000", ChefLevel.HEAD_CHEF);

        kitchen.startPreparingOrder(order, chef);
        assertEquals(OrderStatus.PREPARING, order.getStatus());
        assertEquals(600, flour.getCurrentStock()); // 1000 - 400 = 600

        kitchen.markOrderReady(order);
        assertEquals(OrderStatus.READY, order.getStatus());

        kitchen.serveOrder(order);
        assertEquals(OrderStatus.SERVED, order.getStatus());

        // Cannot cancel served order
        assertThrows(IllegalStateException.class, () -> kitchen.cancelOrder(order, "Customer left"));
    }

    @Test
    @DisplayName("Should reject preparation if inventory is insufficient")
    void testInsufficientStockRejection() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        KitchenService kitchen = new KitchenService(publisher);

        Ingredient butter = new Ingredient("ING-BTR", "Butter", IngredientType.SOLID, MeasurementUnit.GRAMS, 100);
        RecipeIngredient ri = new RecipeIngredient(butter, 80, MeasurementUnit.GRAMS);
        Recipe recipe = new Recipe("REC-CROISSANT", List.of("Bake"), 20, List.of(ri));
        Dish croissant = new Dish("DISH-CR", "Croissant", "Butter croissant", DietaryType.VEG, 150.0, recipe);

        Order order = new Order("ORD-202");
        order.addItem(new OrderItem("IT-2", new BaseDishItem(croissant), 2)); // Needs 160g, but only 100g available

        Chef chef = new Chef("CHEF-2", "Ranveer Brar", "ranveer@test.com", "9911111111", ChefLevel.SOUS_CHEF);

        assertThrows(InsufficientInventoryException.class, () -> kitchen.startPreparingOrder(order, chef));
        assertEquals(OrderStatus.PLACED, order.getStatus()); // Remains PLACED
        assertEquals(100, butter.getCurrentStock()); // Stock unaffected
    }

    @Test
    @DisplayName("Should prevent serving an order before it is READY")
    void testCannotServeBeforeReady() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        KitchenService kitchen = new KitchenService(publisher);

        Order order = new Order("ORD-NOT-READY");
        assertThrows(IllegalStateException.class, () -> kitchen.serveOrder(order));
    }

    @Test
    @DisplayName("Should allow cancelling orders before they are SERVED")
    void testCancelUnservedOrder() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        KitchenService kitchen = new KitchenService(publisher);

        Order order = new Order("ORD-CANCEL-ME");
        kitchen.cancelOrder(order, "Changed mind");
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
