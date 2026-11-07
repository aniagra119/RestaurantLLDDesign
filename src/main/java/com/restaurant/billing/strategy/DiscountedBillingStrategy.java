package com.restaurant.billing.strategy;

import com.restaurant.billing.Bill;
import com.restaurant.model.Order;

import java.util.List;

public class DiscountedBillingStrategy implements BillingStrategy {
    private final double taxRate;
    private final double serviceChargeRate;
    private final double discountPercent;

    public DiscountedBillingStrategy(double taxRate, double serviceChargeRate, double discountPercent) {
        if (taxRate < 0 || serviceChargeRate < 0 || discountPercent < 0 || discountPercent > 1.0) {
            throw new IllegalArgumentException("Invalid discount or tax rate configuration");
        }
        this.taxRate = taxRate;
        this.serviceChargeRate = serviceChargeRate;
        this.discountPercent = discountPercent;
    }

    @Override
    public Bill calculateBill(String billId, List<Order> orders) {
        double subtotal = orders.stream()
                .mapToDouble(Order::calculateSubtotal)
                .sum();

        double discount = subtotal * discountPercent;
        double discountedSubtotal = subtotal - discount;

        double tax = discountedSubtotal * taxRate;
        double serviceCharge = discountedSubtotal * serviceChargeRate;
        double total = discountedSubtotal + tax + serviceCharge;

        return new Bill(billId, subtotal, tax, serviceCharge, discount, total);
    }
}
