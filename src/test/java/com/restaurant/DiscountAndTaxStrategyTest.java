package com.restaurant;

import com.restaurant.decorator.BaseDishItem;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.OrderChannel;
import com.restaurant.enums.Unit;
import com.restaurant.model.Bill;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;
import com.restaurant.strategy.billing.DiscountedBillingStrategy;
import com.restaurant.strategy.billing.StandardBillingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Billing Strategy, Discounts & Financial Math Precision Test Suite")
class DiscountAndTaxStrategyTest {

    @Test
    @DisplayName("Should apply promotional discount and compute taxes on discounted subtotal")
    void testDiscountedBillingStrategy() {
        Cuisine cuisine = new Cuisine("C-1", "Italian", CuisineType.ITALIAN);
        Dish dish = new Dish("D-1", "Truffle Risotto", "Arborio rice", DietaryType.VEG, cuisine,
                new Recipe("R-1", "Risotto", List.of("Stir"), 5, 20,
                        List.of(new RecipeIngredient(new Ingredient("I-1", "Rice", Unit.GRAM, 1000), 100, Unit.GRAM))));

        MenuItem menuItem = new MenuItem("MI-1", dish, BigDecimal.valueOf(1000.00));

        Order order = new Order("ORD-DISC", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-1", new BaseDishItem(menuItem), 1, ""));

        // 20% Discount, 5% Tax, 10% Service Charge
        DiscountedBillingStrategy discountStrategy = new DiscountedBillingStrategy(
                BigDecimal.valueOf(0.05),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.20)
        );

        Bill bill = discountStrategy.computeBill("SESS-10", List.of(order));

        // Subtotal: 1000.00
        // Discount 20%: 200.00
        // Discounted Subtotal: 800.00
        // Tax 5% on 800: 40.00
        // Service Charge 10% on 800: 80.00
        // Total Payable: 1000 - 200 + 40 + 80 = 920.00
        assertEquals(new BigDecimal("1000.00"), bill.getItemSubtotal());
        assertEquals(new BigDecimal("200.00"), bill.getDiscountAmount());
        assertEquals(new BigDecimal("40.00"), bill.getTaxAmount());
        assertEquals(new BigDecimal("80.00"), bill.getServiceCharge());
        assertEquals(new BigDecimal("920.00"), bill.getTotalPayable());
    }

    @Test
    @DisplayName("Should handle empty orders list with zero subtotal cleanly")
    void testEmptyOrdersList() {
        StandardBillingStrategy strategy = StandardBillingStrategy.withDefaultRates();
        Bill bill = strategy.computeBill("SESS-EMPTY", List.of());

        assertEquals(new BigDecimal("0.00"), bill.getItemSubtotal());
        assertEquals(new BigDecimal("0.00"), bill.getTaxAmount());
        assertEquals(new BigDecimal("0.00"), bill.getServiceCharge());
        assertEquals(new BigDecimal("0.00"), bill.getTotalPayable());
    }

    @Test
    @DisplayName("Should preserve exact decimal precision without floating point errors on irregular prices")
    void testIrregularDecimalPrecision() {
        Cuisine cuisine = new Cuisine("C-2", "French", CuisineType.FRENCH);
        Dish pastry = new Dish("D-2", "Croissant", "Flaky pastry", DietaryType.VEG, cuisine,
                new Recipe("R-2", "Croissant", List.of("Bake"), 10, 15,
                        List.of(new RecipeIngredient(new Ingredient("I-2", "Butter", Unit.GRAM, 500), 50, Unit.GRAM))));

        // Irregular price with decimals: 133.33
        MenuItem item = new MenuItem("MI-2", pastry, new BigDecimal("133.33"));
        Order order = new Order("ORD-DEC", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-DEC", new BaseDishItem(item), 3, "")); // 3 * 133.33 = 399.99

        StandardBillingStrategy strategy = new StandardBillingStrategy(new BigDecimal("0.05"), new BigDecimal("0.10"));
        Bill bill = strategy.computeBill("SESS-DEC", List.of(order));

        assertEquals(new BigDecimal("399.99"), bill.getItemSubtotal());
        // Tax 5% of 399.99 = 19.9995 -> 20.00 (HALF_UP)
        assertEquals(new BigDecimal("20.00"), bill.getTaxAmount());
        // Service 10% of 399.99 = 39.999 -> 40.00 (HALF_UP)
        assertEquals(new BigDecimal("40.00"), bill.getServiceCharge());
        // Total = 399.99 + 20.00 + 40.00 = 459.99
        assertEquals(new BigDecimal("459.99"), bill.getTotalPayable());
    }
}

