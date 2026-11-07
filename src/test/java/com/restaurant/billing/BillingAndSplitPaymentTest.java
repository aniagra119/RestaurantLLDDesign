package com.restaurant.billing;

import com.restaurant.billing.strategy.BillingStrategy;
import com.restaurant.billing.strategy.DiscountedBillingStrategy;
import com.restaurant.billing.strategy.StandardBillingStrategy;
import com.restaurant.customization.BaseDishItem;
import com.restaurant.enums.BillStatus;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.PaymentMethod;
import com.restaurant.exception.InvalidPaymentException;
import com.restaurant.model.Dish;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Recipe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Billing Strategy & Split Payment Tests")
class BillingAndSplitPaymentTest {

    private Order createSampleOrder() {
        Recipe recipe = new Recipe("REC-1", List.of("Cook"), 10, List.of());
        Dish pizza = new Dish("DISH-PZ", "Margherita", "Cheese Pizza", DietaryType.VEG, 500.0, recipe);
        Dish pasta = new Dish("DISH-PA", "Penne Arrabbiata", "Spicy pasta", DietaryType.VEG, 300.0, recipe);

        Order order = new Order("ORD-101");
        order.addItem(new OrderItem("ITEM-1", new BaseDishItem(pizza), 2)); // 1000
        order.addItem(new OrderItem("ITEM-2", new BaseDishItem(pasta), 1)); // 300
        return order; // Subtotal = 1300
    }

    @Test
    @DisplayName("Should accurately calculate bill with StandardBillingStrategy")
    void testStandardBillingCalculation() {
        Order order = createSampleOrder();
        BillingStrategy strategy = new StandardBillingStrategy(0.05, 0.10); // 5% tax, 10% service charge

        Bill bill = strategy.calculateBill("BILL-1", List.of(order));
        assertEquals(1300.0, bill.getItemSubtotal());
        assertEquals(65.0, bill.getTaxAmount()); // 1300 * 0.05
        assertEquals(130.0, bill.getServiceCharge()); // 1300 * 0.10
        assertEquals(0.0, bill.getDiscountAmount());
        assertEquals(1495.0, bill.getTotalPayable());
        assertEquals(BillStatus.UNPAID, bill.getStatus());
        assertFalse(bill.isFullyPaid());
    }

    @Test
    @DisplayName("Should accurately calculate bill with DiscountedBillingStrategy")
    void testDiscountedBillingCalculation() {
        Order order = createSampleOrder(); // Subtotal 1300
        BillingStrategy strategy = new DiscountedBillingStrategy(0.05, 0.05, 0.20); // 20% discount

        Bill bill = strategy.calculateBill("BILL-2", List.of(order));
        assertEquals(1300.0, bill.getItemSubtotal());
        assertEquals(260.0, bill.getDiscountAmount()); // 1300 * 0.20
        // Discounted base = 1040
        assertEquals(52.0, bill.getTaxAmount()); // 1040 * 0.05
        assertEquals(52.0, bill.getServiceCharge()); // 1040 * 0.05
        assertEquals(1144.0, bill.getTotalPayable());
    }

    @Test
    @DisplayName("Should support split payments across multiple methods until fully paid")
    void testSplitPayments() {
        Bill bill = new Bill("BILL-SPLIT", 1000.0, 50.0, 50.0, 0.0, 1100.0);
        assertEquals(1100.0, bill.getRemainingAmount());
        assertEquals(BillStatus.UNPAID, bill.getStatus());

        // Friend 1 pays ₹500 via UPI
        bill.addPayment(new Payment("PAY-1", 500.0, PaymentMethod.UPI, "UPI-REF-101"));
        assertEquals(600.0, bill.getRemainingAmount());
        assertEquals(BillStatus.PARTIALLY_PAID, bill.getStatus());
        assertFalse(bill.isFullyPaid());

        // Friend 2 pays ₹400 via Credit Card
        bill.addPayment(new Payment("PAY-2", 400.0, PaymentMethod.CREDIT_CARD, "CARD-REF-202"));
        assertEquals(200.0, bill.getRemainingAmount());
        assertEquals(BillStatus.PARTIALLY_PAID, bill.getStatus());

        // Friend 3 pays remaining ₹200 via Cash
        bill.addPayment(new Payment("PAY-3", 200.0, PaymentMethod.CASH, "CASH-REC"));
        assertEquals(0.0, bill.getRemainingAmount());
        assertEquals(BillStatus.PAID, bill.getStatus());
        assertTrue(bill.isFullyPaid());

        // Attempting to pay more should fail
        assertThrows(IllegalStateException.class, () ->
                bill.addPayment(new Payment("PAY-4", 50.0, PaymentMethod.CASH, "EXTRA")));
    }

    @Test
    @DisplayName("Should reject payment exceeding remaining balance")
    void testOverpaymentRejection() {
        Bill bill = new Bill("BILL-OVER", 500.0, 0.0, 0.0, 0.0, 500.0);
        assertThrows(InvalidPaymentException.class, () ->
                bill.addPayment(new Payment("PAY-EXCEED", 600.0, PaymentMethod.UPI, "UPI-OVER")));
    }

    @Test
    @DisplayName("Should reject invalid tax rates or discount percentages")
    void testInvalidStrategyParameters() {
        assertThrows(IllegalArgumentException.class, () -> new StandardBillingStrategy(-0.05, 0.10));
        assertThrows(IllegalArgumentException.class, () -> new StandardBillingStrategy(0.05, -0.10));
        assertThrows(IllegalArgumentException.class, () -> new DiscountedBillingStrategy(0.05, 0.05, 1.5)); // > 100%
        assertThrows(IllegalArgumentException.class, () -> new DiscountedBillingStrategy(0.05, 0.05, -0.1));
    }

    @Test
    @DisplayName("Should verify all PaymentMethod enum values")
    void testPaymentMethods() {
        assertEquals(4, PaymentMethod.values().length);
        assertNotNull(PaymentMethod.valueOf("CASH"));
        assertNotNull(PaymentMethod.valueOf("CREDIT_CARD"));
        assertNotNull(PaymentMethod.valueOf("DEBIT_CARD"));
        assertNotNull(PaymentMethod.valueOf("UPI"));
    }
}
