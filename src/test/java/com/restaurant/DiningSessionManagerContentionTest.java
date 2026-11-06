package com.restaurant;

import com.restaurant.enums.Department;
import com.restaurant.enums.Permission;
import com.restaurant.model.Customer;
import com.restaurant.model.DiningSession;
import com.restaurant.model.Role;
import com.restaurant.model.Waiter;
import com.restaurant.service.DiningSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiningSessionManager Table Contention & Lifecycle Test Suite")
class DiningSessionManagerContentionTest {

    private DiningSessionManager sessionManager;
    private Waiter waiter;

    @BeforeEach
    void setUp() {
        sessionManager = new DiningSessionManager();
        Role waiterRole = new Role("R-W", "Server", Department.SERVICE, Set.of(Permission.TAKE_ORDER));
        waiter = new Waiter("W-10", "Dinesh", "dinesh@test.com", "9988776655", waiterRole);
    }

    @Test
    @DisplayName("Should prevent multiple hosts from concurrently assigning the same table to different customers")
    void testConcurrentTableAssignmentConflict() throws InterruptedException {
        String contestedTable = "Table-VIP-1";
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Customer customer = new Customer("C-" + index, "Guest-" + index, "900000000" + index, "guest" + index + "@test.com");
                    sessionManager.openSession("SESS-" + index, contestedTable, customer, waiter);
                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    conflictCount.incrementAndGet();
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

        assertEquals(1, successCount.get(), "Exactly one host can successfully seat a customer at Table-VIP-1");
        assertEquals(9, conflictCount.get(), "Remaining 9 attempts must be rejected with IllegalStateException");

        Optional<DiningSession> active = sessionManager.getSessionByTable(contestedTable);
        assertTrue(active.isPresent());
    }

    @Test
    @DisplayName("Should reject closing a session that is still ACTIVE or BILLED without settlement")
    void testCannotCloseUnsettledSession() {
        Customer customer = new Customer("C-20", "Rohit", "9123456789", "rohit@test.com");
        DiningSession session = sessionManager.openSession("SESS-UNSETTLED", "Table-9", customer, waiter);

        assertThrows(IllegalStateException.class, () ->
                sessionManager.closeSession(session.getSessionId()));
    }
}

