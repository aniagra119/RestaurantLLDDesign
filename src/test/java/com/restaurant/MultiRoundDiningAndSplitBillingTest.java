package com.restaurant;

import com.restaurant.decorator.BaseDishItem;
import com.restaurant.enums.BillStatus;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.Department;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.OrderChannel;
import com.restaurant.enums.PaymentMethod;
import com.restaurant.enums.Permission;
import com.restaurant.enums.SessionStatus;
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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Multi-Round Dining Session & Split Billing Test Suite")
class MultiRoundDiningAndSplitBillingTest {

    private Waiter waiter;
    private Customer customer;
    private MenuItem soupItem;
    private MenuItem pastaItem;

    @BeforeEach
    void setUp() {
        Role waiterRole = new Role("ROLE-W", "Server", Department.SERVICE, Set.of(Permission.TAKE_ORDER, Permission.GENERATE_BILL));
        waiter = new Waiter("W-10", "Amit", "amit@restaurant.com", "9876543210", waiterRole);
        customer = new Customer("CUST-01", "Vikram Malhotra", "9988776655", "vikram@example.com");

        Cuisine italian = new Cuisine("CUIS-IT", "Italian", CuisineType.ITALIAN);
        Ingredient tomato = new Ingredient("ING-T", "Tomato", Unit.GRAM, 10000.0);
        Ingredient pasta = new Ingredient("ING-P", "Pasta", Unit.GRAM, 10000.0);

        Dish soup = new Dish("DISH-S", "Minestrone Soup", "Italian vegetable soup", DietaryType.VEG, italian,
                new Recipe("REC-S", "Soup", List.of("Boil"), 5, 10, List.of(new RecipeIngredient(tomato, 100, Unit.GRAM))));
        Dish penne = new Dish("DISH-P", "Penne Arrabiata", "Spicy tomato penne", DietaryType.VEG, italian,
                new Recipe("REC-P", "Pasta", List.of("Cook"), 5, 12, List.of(new RecipeIngredient(pasta, 150, Unit.GRAM))));

        soupItem = new MenuItem("MI-S", soup, BigDecimal.valueOf(200.00));
        pastaItem = new MenuItem("MI-P", penne, BigDecimal.valueOf(400.00));
    }

    @Test
    @DisplayName("Should track multi-round orders in a single visit and generate itemized bill")
    void testMultiRoundOrdersAndBilling() {
        DiningSession session = new DiningSession("SESS-001", "Table-5", customer, waiter);
        assertEquals(SessionStatus.ACTIVE, session.getStatus());

        // Round 1: Starters (2x Minestrone Soup)
        Order round1 = new Order("ORD-01", OrderChannel.DINE_IN);
        round1.addItem(new OrderItem("OI-01", new BaseDishItem(soupItem), 2, "Less spicy"));
        assertEquals(BigDecimal.valueOf(400.00).setScale(2), round1.calculateSubtotal().setScale(2));
        session.addOrder(round1);

        // Round 2: Mains (2x Penne Arrabiata)
        Order round2 = new Order("ORD-02", OrderChannel.DINE_IN);
        round2.addItem(new OrderItem("OI-02", new BaseDishItem(pastaItem), 2, "Extra basil"));
        assertEquals(BigDecimal.valueOf(800.00).setScale(2), round2.calculateSubtotal().setScale(2));
        session.addOrder(round2);

        assertEquals(2, session.getOrders().size());

        // End of visit: Generate Bill with 5% GST and 10% Service Charge
        // Subtotal = 400 + 800 = 1200.00
        // GST (5%) = 60.00
        // Service Charge (10%) = 120.00
        // Total = 1380.00
        StandardBillingStrategy billingStrategy = StandardBillingStrategy.withDefaultRates();
        Bill bill = session.generateBill(billingStrategy);

        assertEquals(SessionStatus.BILLED, session.getStatus());
        assertEquals(new BigDecimal("1200.00"), bill.getItemSubtotal());
        assertEquals(new BigDecimal("60.00"), bill.getTaxAmount());
        assertEquals(new BigDecimal("120.00"), bill.getServiceCharge());
        assertEquals(new BigDecimal("1380.00"), bill.getTotalPayable());
        assertEquals(BillStatus.UNPAID, bill.getBillStatus());

        // Invariant: Once billed, customer cannot order more rounds
        Order round3 = new Order("ORD-03", OrderChannel.DINE_IN);
        assertThrows(IllegalStateException.class, () -> session.addOrder(round3));
    }

    @Test
    @DisplayName("Should support split payment between friends across multiple payment methods")
    void testSplitPayments() {
        DiningSession session = new DiningSession("SESS-002", "Table-8", customer, waiter);

        Order order = new Order("ORD-10", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-10", new BaseDishItem(pastaItem), 5, "")); // 5 * 400 = 2000.00
        session.addOrder(order);

        // Subtotal: 2000, Tax(5%): 100, Service(10%): 200 -> Total: 2300.00
        Bill bill = session.generateBill(StandardBillingStrategy.withDefaultRates());
        assertEquals(new BigDecimal("2300.00"), bill.getTotalPayable());

        // Friend A pays 1000.00 via UPI
        Payment payment1 = new Payment("PAY-01", new BigDecimal("1000.00"), PaymentMethod.UPI, "UPI-REF-12345");
        session.applyPayment(payment1);

        assertEquals(BillStatus.PARTIALLY_PAID, bill.getBillStatus());
        assertEquals(new BigDecimal("1300.00"), bill.getRemainingAmount());
        assertEquals(SessionStatus.BILLED, session.getStatus()); // Session remains BILLED

        // Friend B pays remaining 1300.00 via Credit Card
        Payment payment2 = new Payment("PAY-02", new BigDecimal("1300.00"), PaymentMethod.CREDIT_CARD, "CARD-TXN-67890");
        session.applyPayment(payment2);

        assertEquals(BillStatus.PAID, bill.getBillStatus());
        assertEquals(new BigDecimal("0.00"), bill.getRemainingAmount());
        assertTrue(bill.isFullyPaid());
        assertEquals(SessionStatus.SETTLED, session.getStatus()); // Session automatically becomes SETTLED!
    }

    @Test
    @DisplayName("Should reject overpayment amount exceeding remaining balance")
    void testOverpaymentRejection() {
        DiningSession session = new DiningSession("SESS-003", "Table-2", customer, waiter);
        Order order = new Order("ORD-20", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-20", new BaseDishItem(soupItem), 1, ""));
        session.addOrder(order);

        Bill bill = session.generateBill(StandardBillingStrategy.withDefaultRates());
        BigDecimal total = bill.getTotalPayable();

        // Attempting to pay total + 100.00 should throw IllegalArgumentException
        Payment excessivePayment = new Payment("PAY-BAD", total.add(BigDecimal.valueOf(100)), PaymentMethod.CASH, "CASH");
        assertThrows(IllegalArgumentException.class, () -> session.applyPayment(excessivePayment));
    }
}

