package com.restaurant.billing.strategy;

import com.restaurant.billing.Bill;
import com.restaurant.model.Order;

import java.util.List;

public class StandardBillingStrategy implements BillingStrategy {
    private final double taxRate;
    private final double serviceChargeRate;

    public StandardBillingStrategy(double taxRate, double serviceChargeRate) {
        if (taxRate < 0 || serviceChargeRate < 0) {
            throw new IllegalArgumentException("Rates cannot be negative");
        }
        this.taxRate = taxRate;
        this.serviceChargeRate = serviceChargeRate;
    }

    @Override
    public Bill calculateBill(String billId, List<Order> orders) {
        double subtotal = orders.stream()
                .mapToDouble(Order::calculateSubtotal)
                .sum();

        double tax = subtotal * taxRate;
        double serviceCharge = subtotal * serviceChargeRate;
        double total = subtotal + tax + serviceCharge;

        return new Bill(billId, subtotal, tax, serviceCharge, 0.0, total);
    }
}

