package com.restaurant.strategy.billing;

import com.restaurant.enums.OrderStatus;
import com.restaurant.model.Bill;
import com.restaurant.model.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public class DiscountedBillingStrategy implements BillingStrategy {
    private final BigDecimal taxRate;
    private final BigDecimal serviceChargeRate;
    private final BigDecimal discountRate; // e.g. 0.15 for 15% discount

    public DiscountedBillingStrategy(BigDecimal taxRate, BigDecimal serviceChargeRate, BigDecimal discountRate) {
        this.taxRate = taxRate != null ? taxRate : BigDecimal.valueOf(0.05);
        this.serviceChargeRate = serviceChargeRate != null ? serviceChargeRate : BigDecimal.valueOf(0.10);
        this.discountRate = discountRate != null ? discountRate : BigDecimal.ZERO;
    }

    @Override
    public Bill computeBill(String sessionId, List<Order> orders) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Order order : orders) {
            if (order.getStatus() != OrderStatus.CANCELLED) {
                subtotal = subtotal.add(order.calculateSubtotal());
            }
        }

        BigDecimal discount = subtotal.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountedSubtotal = subtotal.subtract(discount);
        BigDecimal tax = discountedSubtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal serviceCharge = discountedSubtotal.multiply(serviceChargeRate).setScale(2, RoundingMode.HALF_UP);

        String billId = "BILL-DISC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Bill(billId, sessionId, subtotal, tax, serviceCharge, discount);
    }
}

