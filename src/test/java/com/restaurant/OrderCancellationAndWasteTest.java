package com.restaurant;

import com.restaurant.decorator.BaseDishItem;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.Department;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.OrderChannel;
import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.Permission;
import com.restaurant.enums.Unit;
import com.restaurant.event.KitchenEventPublisher;
import com.restaurant.event.listener.AuditLogListener;
import com.restaurant.model.Bill;
import com.restaurant.model.Chef;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Customer;
import com.restaurant.model.DiningSession;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;
import com.restaurant.model.Role;
import com.restaurant.model.Waiter;
import com.restaurant.service.KitchenService;
import com.restaurant.strategy.billing.StandardBillingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Cancellation, Voiding & Kitchen Waste Test Suite")
class OrderCancellationAndWasteTest {

    private KitchenService kitchenService;
    private KitchenEventPublisher eventPublisher;
    private AuditLogListener auditLog;
    private Chef chef;
    private MenuItem burgerItem;

    @BeforeEach
    void setUp() {
        eventPublisher = new KitchenEventPublisher();
        auditLog = new AuditLogListener();
        eventPublisher.registerListener(auditLog);
        kitchenService = new KitchenService(eventPublisher);

        Role chefRole = new Role("R-CHEF", "Chef", Department.KITCHEN, Set.of(Permission.PREPARE_FOOD));
        chef = new Chef("C-01", "Gordon", "gordon@kitchen.com", "9988776655", chefRole);

        Cuisine american = new Cuisine("C-US", "American", CuisineType.CONTINENTAL);
        Ingredient patty = new Ingredient("ING-PATTY", "Plant Patty", Unit.PIECE, 100.0);
        Dish burger = new Dish("D-BGR", "Smash Burger", "Signature burger", DietaryType.VEGAN, american,
                new Recipe("R-BGR", "Burger", List.of("Grill patty"), 5, 5, List.of(new RecipeIngredient(patty, 1.0, Unit.PIECE))));

        burgerItem = new MenuItem("MI-BGR", burger, BigDecimal.valueOf(350.00));
    }

    @Test
    @DisplayName("Should cancel an order in RECEIVED state successfully")
    void testCancelReceivedOrder() {
        Order order = new Order("ORD-CANCEL-1", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-1", new BaseDishItem(burgerItem), 1, ""));

        kitchenService.cancelOrder(order, "Customer changed mind");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(1, auditLog.getEventCount());
        assertEquals("ORDER_CANCELLED", auditLog.getEventLog().getFirst().getEventType());
    }

    @Test
    @DisplayName("Should reject cancellation once order has been served to table")
    void testCannotCancelServedOrder() {
        Order order = new Order("ORD-SERVED", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-2", new BaseDishItem(burgerItem), 1, ""));

        kitchenService.prepareOrder(order, chef);
        kitchenService.markOrderServed(order);
        assertEquals(OrderStatus.SERVED, order.getStatus());

        assertThrows(IllegalStateException.class, () ->
                kitchenService.cancelOrder(order, "Too late to cancel"));
    }

    @Test
    @DisplayName("Should omit cancelled orders from final bill subtotal calculation")
    void testCancelledOrderOmittedFromBill() {
        Role waiterRole = new Role("R-W", "Server", Department.SERVICE, Set.of(Permission.TAKE_ORDER, Permission.GENERATE_BILL));
        Waiter waiter = new Waiter("W-1", "Sunil", "sunil@trattoria.com", "9876543210", waiterRole);
        Customer customer = new Customer("C-1", "Alia", "9123456789", "alia@example.com");

        DiningSession session = new DiningSession("SESS-CANCEL-BILL", "Table-3", customer, waiter);

        // Round 1: Kept order (1x Burger = 350.00)
        Order order1 = new Order("ORD-KEEP", OrderChannel.DINE_IN);
        order1.addItem(new OrderItem("OI-KEEP", new BaseDishItem(burgerItem), 1, ""));
        session.addOrder(order1);

        // Round 2: Cancelled order (2x Burgers = 700.00)
        Order order2 = new Order("ORD-VOID", OrderChannel.DINE_IN);
        order2.addItem(new OrderItem("OI-VOID", new BaseDishItem(burgerItem), 2, ""));
        session.addOrder(order2);
        kitchenService.cancelOrder(order2, "Wrong table entry");

        // Bill generated: Should only bill order1 (350.00), not order2!
        Bill bill = session.generateBill(StandardBillingStrategy.withDefaultRates());

        assertEquals(new BigDecimal("350.00"), bill.getItemSubtotal());
        // Tax 5%: 17.50, Service 10%: 35.00 -> Total: 402.50
        assertEquals(new BigDecimal("17.50"), bill.getTaxAmount());
        assertEquals(new BigDecimal("35.00"), bill.getServiceCharge());
        assertEquals(new BigDecimal("402.50"), bill.getTotalPayable());
    }
}

