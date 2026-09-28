package com.restaurant.billing.strategy;

import com.restaurant.billing.Bill;
import com.restaurant.model.Order;

import java.util.List;

public interface BillingStrategy {
    Bill calculateBill(String billId, List<Order> orders);
}
