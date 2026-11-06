package com.restaurant;

import com.restaurant.decorator.BaseDishItem;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.Department;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.OrderChannel;
import com.restaurant.enums.Permission;
import com.restaurant.enums.Unit;
import com.restaurant.model.Chef;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;
import com.restaurant.model.Role;
import com.restaurant.service.KitchenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Concurrent Inventory Depletion & Stock Starvation Test Suite")
class ConcurrentInventoryStressTest {

    @Test
    @DisplayName("Should prevent negative inventory under high-concurrency race condition when 20 chefs cook limited stock")
    void testConcurrentInventoryDepletion() throws InterruptedException {
        // Stock only has 500 grams. Each dish requires 100 grams. Exactly 5 portions available!
        Ingredient cheese = new Ingredient("ING-CHEESE", "Fresh Burrata", Unit.GRAM, 500.0);

        Cuisine italian = new Cuisine("C-IT", "Italian", CuisineType.ITALIAN);
        Dish burrataDish = new Dish("D-BURRATA", "Burrata Salad", "Burrata with heirloom tomatoes",
                DietaryType.VEG, italian,
                new Recipe("R-BURR", "Burrata Salad", List.of("Plate burrata"), 5, 0,
                        List.of(new RecipeIngredient(cheese, 100.0, Unit.GRAM))));

        MenuItem burrataItem = new MenuItem("MI-BURR", burrataDish, BigDecimal.valueOf(600.00));
        KitchenService kitchenService = new KitchenService();

        Role chefRole = new Role("R-CHEF", "Line Cook", Department.KITCHEN, Set.of(Permission.PREPARE_FOOD));
        Chef chef = new Chef("CHEF-1", "Vikas", "vikas@restaurant.com", "9876543210", chefRole);

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger starvationCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Order order = new Order("ORD-STRESS-" + index, OrderChannel.DINE_IN);
                    order.addItem(new OrderItem("OI-" + index, new BaseDishItem(burrataItem), 1, ""));
                    kitchenService.prepareOrder(order, chef);
                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    starvationCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Invariant: Exactly 5 portions could be cooked!
        assertEquals(5, successCount.get(), "Exactly 5 orders should succeed due to 500g / 100g = 5 stock boundary");
        assertEquals(15, starvationCount.get(), "15 orders must fail gracefully with stock starvation IllegalStateException");
        assertEquals(0.0, cheese.getAvailableQuantity(), 0.001, "Inventory must be cleanly drained to zero, never negative");
    }

    @Test
    @DisplayName("Should atomically handle simultaneous multi-threaded restock and deduction without lost updates")
    void testConcurrentRestockAndDeduction() throws InterruptedException {
        Ingredient oil = new Ingredient("ING-OIL", "Canola Oil", Unit.MILLILITER, 1000.0);
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        // 10 threads restock 50ml, 10 threads deduct 50ml
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    oil.restock(50.0);
                } catch (Exception ignored) {
                } finally {
                    finishLatch.countDown();
                }
            });
            executor.submit(() -> {
                try {
                    startLatch.await();
                    oil.deductStock(50.0);
                } catch (Exception ignored) {
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // 1000 + (10 * 50) - (10 * 50) = 1000
        assertEquals(1000.0, oil.getAvailableQuantity(), 0.001, "Final inventory quantity must balance out accurately to 1000.0");
    }
}

