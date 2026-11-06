package com.restaurant;

import com.restaurant.decorator.BaseDishItem;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.Department;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.OrderChannel;
import com.restaurant.enums.PaymentMethod;
import com.restaurant.enums.Permission;
import com.restaurant.enums.Unit;
import com.restaurant.model.Bill;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Customer;
import com.restaurant.model.DiningSession;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Payment;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;
import com.restaurant.model.Role;
import com.restaurant.model.Waiter;
import com.restaurant.strategy.billing.StandardBillingStrategy;
import org.junit.jupiter.api.BeforeEach;
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

@DisplayName("Concurrent Multi-Threaded Ordering & Contention Stress Test Suite")
class ConcurrentOrderingStressTest {

    private Waiter waiter;
    private Customer customer;
    private MenuItem pastaItem;

    @BeforeEach
    void setUp() {
        Role waiterRole = new Role("R-W", "Server", Department.SERVICE, Set.of(Permission.TAKE_ORDER, Permission.GENERATE_BILL));
        waiter = new Waiter("W-1", "Rajesh", "rajesh@trattoria.com", "9988776655", waiterRole);
        customer = new Customer("C-1", "Pooja Hegde", "9123456780", "pooja@example.com");

        Cuisine italian = new Cuisine("C-IT", "Italian", CuisineType.ITALIAN);
        Ingredient flour = new Ingredient("ING-FLOUR", "Durum Flour", Unit.GRAM, 50000.0);
        Dish pasta = new Dish("D-PASTA", "Spaghetti Aglio Olio", "Garlic olive oil spaghetti",
                DietaryType.VEGAN, italian,
                new Recipe("R-AGLIO", "Aglio Olio", List.of("Toss with garlic"), 5, 8, List.of(new RecipeIngredient(flour, 100, Unit.GRAM))));

        pastaItem = new MenuItem("MI-PASTA", pasta, BigDecimal.valueOf(350.00));
    }

    @Test
    @DisplayName("Should safely handle 20 concurrent waiters adding orders to the same dining session without race conditions")
    void testConcurrentOrderPlacementOnSession() throws InterruptedException {
        DiningSession session = new DiningSession("SESS-CONC-1", "Table-10", customer, waiter);
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // Synchronize all threads to fire simultaneously
                    Order order = new Order("ORD-CONC-" + index, OrderChannel.DINE_IN);
                    order.addItem(new OrderItem("OI-" + index, new BaseDishItem(pastaItem), 1, ""));
                    session.addOrder(order);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Release threads
        boolean completed = finishLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "All threads should complete execution within timeout");
        assertEquals(20, session.getOrders().size(), "Session must safely accumulate all 20 orders without loss");
    }

    @Test
    @DisplayName("Should prevent any concurrent orders once session transitions to BILLED")
    void testConcurrentOrderRejectionAfterBilled() throws InterruptedException {
        DiningSession session = new DiningSession("SESS-CONC-2", "Table-7", customer, waiter);
        Order initialOrder = new Order("ORD-INIT", OrderChannel.DINE_IN);
        initialOrder.addItem(new OrderItem("OI-INIT", new BaseDishItem(pastaItem), 2, ""));
        session.addOrder(initialOrder);

        // Bill generated
        session.generateBill(StandardBillingStrategy.withDefaultRates());

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);
        AtomicInteger rejectedCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Order lateOrder = new Order("ORD-LATE-" + index, OrderChannel.DINE_IN);
                    lateOrder.addItem(new OrderItem("OI-LATE-" + index, new BaseDishItem(pastaItem), 1, ""));
                    session.addOrder(lateOrder);
                } catch (IllegalStateException e) {
                    rejectedCount.incrementAndGet();
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

        assertEquals(10, rejectedCount.get(), "All 10 late order attempts must be rejected with IllegalStateException");
        assertEquals(1, session.getOrders().size(), "Only initial order should remain in session");
    }

    @Test
    @DisplayName("Should prevent split payments from exceeding total payable under concurrent settlement attempts")
    void testConcurrentPaymentRaceConditionProtection() throws InterruptedException {
        DiningSession session = new DiningSession("SESS-CONC-3", "Table-1", customer, waiter);
        Order order = new Order("ORD-PAY", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-P", new BaseDishItem(pastaItem), 1, ""));
        session.addOrder(order);

        Bill bill = session.generateBill(StandardBillingStrategy.withDefaultRates());
        BigDecimal total = bill.getTotalPayable(); // Total payable

        // Attempt 5 concurrent payments, each trying to pay 60% of the total
        int threadCount = 5;
        BigDecimal attemptAmount = total.multiply(BigDecimal.valueOf(0.6)).setScale(2, java.math.RoundingMode.HALF_UP);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger rejectedCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Payment p = new Payment("PAY-RACE-" + index, attemptAmount, PaymentMethod.UPI, "REF-" + index);
                    session.applyPayment(p);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    rejectedCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(1, successCount.get(), "Only 1 payment of 60% can succeed without exceeding total balance");
        assertEquals(4, rejectedCount.get(), "Remaining 4 payments must be rejected");
        assertTrue(bill.getRemainingAmount().compareTo(BigDecimal.ZERO) > 0);
    }
}
