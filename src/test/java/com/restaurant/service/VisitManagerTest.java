package com.restaurant.service;

import com.restaurant.billing.Bill;
import com.restaurant.billing.Payment;
import com.restaurant.billing.strategy.StandardBillingStrategy;
import com.restaurant.customization.BaseDishItem;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.PaymentMethod;
import com.restaurant.enums.VisitStatus;
import com.restaurant.enums.WaiterLevel;
import com.restaurant.event.OrderEventPublisher;
import com.restaurant.exception.InvalidVisitStateException;
import com.restaurant.exception.TableAlreadyOccupiedException;
import com.restaurant.model.Customer;
import com.restaurant.model.CustomerVisit;
import com.restaurant.model.Dish;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Recipe;
import com.restaurant.model.Table;
import com.restaurant.model.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Visit Manager & Multi-Round Visit Lifecycle Tests")
class VisitManagerTest {

    @Test
    @DisplayName("Should execute complete multi-round visit lifecycle from seating to settlement")
    void testCompleteVisitLifecycle() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        VisitManager manager = new VisitManager(publisher);

        Customer customer = new Customer("CUST-10", "Neha", "9811122233", "neha@test.com");
        Table table = new Table("T-1", 4);
        Waiter waiter = new Waiter("W-1", "Sanjay", "sanjay@test.com", "9811100000", WaiterLevel.SENIOR);

        CustomerVisit visit = manager.startVisit(customer, table, waiter);
        assertTrue(table.isOccupied());
        assertTrue(waiter.getAssignedTables().contains(table));
        assertEquals(VisitStatus.ACTIVE, visit.getStatus());

        // Cannot start another visit on occupied table
        assertThrows(TableAlreadyOccupiedException.class, () ->
                manager.startVisit(new Customer("CUST-2", "Rohan", "9800000000", "r@test.com"), table, waiter));

        // Round 1: Appetizers
        Recipe recipe = new Recipe("R-1", List.of("Cook"), 10, List.of());
        Dish garlicBread = new Dish("D-1", "Garlic Bread", "Crispy bread", DietaryType.VEG, 200.0, recipe);
        Order round1 = new Order("ORD-R1");
        round1.addItem(new OrderItem("IT-1", new BaseDishItem(garlicBread), 2)); // 400
        manager.placeOrder(visit.getVisitId(), round1);

        // Round 2: Main Course
        Dish risotto = new Dish("D-2", "Mushroom Risotto", "Creamy risotto", DietaryType.VEG, 450.0, recipe);
        Order round2 = new Order("ORD-R2");
        round2.addItem(new OrderItem("IT-2", new BaseDishItem(risotto), 1)); // 450
        manager.placeOrder(visit.getVisitId(), round2);

        assertEquals(2, visit.getOrders().size());

        // Generate Bill (Subtotal = 850, 10% tax, 5% service = 977.5)
        Bill bill = manager.generateBill(visit.getVisitId(), new StandardBillingStrategy(0.10, 0.05));
        assertEquals(VisitStatus.BILLED, visit.getStatus());
        assertEquals(850.0, bill.getItemSubtotal());
        assertEquals(977.5, bill.getTotalPayable());

        // Cannot place more orders once billed
        Order round3 = new Order("ORD-R3");
        assertThrows(InvalidVisitStateException.class, () -> manager.placeOrder(visit.getVisitId(), round3));

        // Cannot end visit before full payment
        assertThrows(InvalidVisitStateException.class, () -> manager.endVisit(visit.getVisitId()));

        // Pay full amount
        manager.settlePayment(visit.getVisitId(), new Payment("PAY-1", 977.5, PaymentMethod.UPI, "UPI-REF"));
        assertTrue(bill.isFullyPaid());

        // End visit -> Table freed and waiter released
        manager.endVisit(visit.getVisitId());
        assertEquals(VisitStatus.COMPLETED, visit.getStatus());
        assertFalse(table.isOccupied());
        assertFalse(waiter.getAssignedTables().contains(table));
    }

    @Test
    @DisplayName("Should track multiple active visits on distinct tables independently")
    void testMultipleIndependentActiveVisits() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        VisitManager manager = new VisitManager(publisher);

        Table table1 = new Table("T-1", 2);
        Table table2 = new Table("T-2", 4);
        Waiter waiter = new Waiter("W-1", "Kunal", "kunal@test.com", "9800000001", WaiterLevel.JUNIOR);

        CustomerVisit visit1 = manager.startVisit(new Customer("C-1", "Aman", "9100000001", "a@test.com"), table1, waiter);
        CustomerVisit visit2 = manager.startVisit(new Customer("C-2", "Bina", "9100000002", "b@test.com"), table2, waiter);

        assertEquals(2, manager.getActiveVisits().size());
        assertEquals(visit1, manager.getVisit(visit1.getVisitId()));
        assertEquals(visit2, manager.getVisit(visit2.getVisitId()));
        assertEquals(2, waiter.getAssignedTables().size());
    }

    @Test
    @DisplayName("Should reject operations on non-existent visit IDs")
    void testNonExistentVisitOperations() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        VisitManager manager = new VisitManager(publisher);

        assertThrows(IllegalArgumentException.class, () -> manager.placeOrder("NON-EXISTENT", new Order("O-1")));
        assertThrows(IllegalArgumentException.class, () -> manager.generateBill("NON-EXISTENT", new StandardBillingStrategy(0.05, 0.05)));
        assertThrows(IllegalArgumentException.class, () -> manager.settlePayment("NON-EXISTENT", new Payment("P-1", 100, PaymentMethod.CASH, "REF")));
        assertThrows(IllegalArgumentException.class, () -> manager.endVisit("NON-EXISTENT"));
    }

    @Test
    @DisplayName("Should reject payment on CustomerVisit before bill generation")
    void testPaymentBeforeBillThrows() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        VisitManager manager = new VisitManager(publisher);

        Table table = new Table("T-9", 4);
        Waiter waiter = new Waiter("W-9", "Waiter 9", "w9@test.com", "9800000009", WaiterLevel.JUNIOR);
        CustomerVisit visit = manager.startVisit(new Customer("C-9", "Customer 9", "9100000009", "c9@test.com"), table, waiter);

        assertThrows(InvalidVisitStateException.class, () ->
                visit.makePayment(new Payment("P-PRE", 100.0, PaymentMethod.CASH, "REF")));
    }
}
