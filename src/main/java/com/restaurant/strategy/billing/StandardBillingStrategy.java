package com.restaurant.strategy.billing;

import com.restaurant.enums.OrderStatus;
import com.restaurant.model.Bill;
import com.restaurant.model.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public class StandardBillingStrategy implements BillingStrategy {
    private final BigDecimal taxRate;           // e.g. 0.05 for 5% GST
    private final BigDecimal serviceChargeRate; // e.g. 0.10 for 10% Service Charge

    public StandardBillingStrategy(BigDecimal taxRate, BigDecimal serviceChargeRate) {
        this.taxRate = taxRate != null ? taxRate : BigDecimal.valueOf(0.05);
        this.serviceChargeRate = serviceChargeRate != null ? serviceChargeRate : BigDecimal.valueOf(0.10);
    }

    public static StandardBillingStrategy withDefaultRates() {
        return new StandardBillingStrategy(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.10));
    }

    @Override
    public Bill computeBill(String sessionId, List<Order> orders) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Order order : orders) {
            // Only non-cancelled orders are billed
            if (order.getStatus() != OrderStatus.CANCELLED) {
                subtotal = subtotal.add(order.calculateSubtotal());
            }
        }

        BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal serviceCharge = subtotal.multiply(serviceChargeRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        String billId = "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Bill(billId, sessionId, subtotal, tax, serviceCharge, discount);
    }
}

