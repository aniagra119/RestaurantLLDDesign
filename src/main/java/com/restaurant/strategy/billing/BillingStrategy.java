package com.restaurant.strategy.billing;

import com.restaurant.model.Bill;
import com.restaurant.model.Order;

import java.util.List;

public interface BillingStrategy {
    Bill computeBill(String sessionId, List<Order> orders);
}

