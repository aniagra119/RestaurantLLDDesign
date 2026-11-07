package com.restaurant.concurrency;

import com.restaurant.enums.IngredientType;
import com.restaurant.enums.MeasurementUnit;
import com.restaurant.enums.WaiterLevel;
import com.restaurant.event.OrderEventPublisher;
import com.restaurant.exception.InsufficientInventoryException;
import com.restaurant.exception.TableAlreadyOccupiedException;
import com.restaurant.model.Customer;
import com.restaurant.model.Ingredient;
import com.restaurant.model.Table;
import com.restaurant.model.Waiter;
import com.restaurant.service.VisitManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Concurrency & Multi-Threading Stress Tests")
class ConcurrentVisitAndStockStressTest {

    @Test
    @DisplayName("Should prevent double-booking on concurrent table visit openings")
    void testConcurrentTableOccupancy() throws InterruptedException {
        OrderEventPublisher publisher = new OrderEventPublisher();
        VisitManager manager = new VisitManager(publisher);

        Table table = new Table("T-VIP", 4);
        Waiter waiter = new Waiter("W-1", "Karan", "karan@test.com", "9876543210", WaiterLevel.MANAGER);

        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Customer customer = new Customer("C-" + idx, "Guest " + idx, "999990000" + idx, "g" + idx + "@test.com");
                    manager.startVisit(customer, table, waiter);
                    successCount.incrementAndGet();
                } catch (TableAlreadyOccupiedException e) {
                    failureCount.incrementAndGet();
                } catch (InterruptedException ignored) {
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        assertEquals(1, successCount.get(), "Exactly one thread must successfully occupy the table");
        assertEquals(threads - 1, failureCount.get(), "All other threads must receive TableAlreadyOccupiedException");
        assertTrue(table.isOccupied());
    }

    @Test
    @DisplayName("Should maintain atomic consistency during concurrent inventory deductions")
    void testConcurrentStockDeduction() throws InterruptedException {
        // 1000g available. 20 threads each attempt to deduct 100g.
        // Exactly 10 must succeed, and 10 must fail with InsufficientInventoryException.
        // Final stock must be exactly 0g (never negative).
        Ingredient cheese = new Ingredient("ING-CHZ", "Cheddar", IngredientType.SOLID, MeasurementUnit.GRAMS, 1000.0);

        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threads);

        AtomicInteger successDeductions = new AtomicInteger(0);
        AtomicInteger failedDeductions = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    cheese.deductStock(100.0);
                    successDeductions.incrementAndGet();
                } catch (InsufficientInventoryException e) {
                    failedDeductions.incrementAndGet();
                } catch (InterruptedException ignored) {
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        executor.shutdown();

        assertEquals(10, successDeductions.get(), "Exactly 10 deductions of 100g can succeed for 1000g initial stock");
        assertEquals(10, failedDeductions.get(), "10 attempts must fail with InsufficientInventoryException");
        assertEquals(0.0, cheese.getCurrentStock(), 0.001, "Final stock must be exactly 0g");
    }
}

