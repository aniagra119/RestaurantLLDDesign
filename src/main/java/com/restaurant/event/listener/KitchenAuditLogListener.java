package com.restaurant.event.listener;

import com.restaurant.event.OrderEventListener;
import com.restaurant.event.OrderPlacedEvent;
import com.restaurant.event.OrderStatusChangedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitchenAuditLogListener implements OrderEventListener {
    private final List<String> auditLogs = new ArrayList<>();

    public synchronized List<String> getAuditLogs() {
        return Collections.unmodifiableList(auditLogs);
    }

    @Override
    public synchronized void onOrderPlaced(OrderPlacedEvent event) {
        auditLogs.add("AUDIT: Order placed " + event.getOrder().getOrderId());
    }

    @Override
    public synchronized void onOrderStatusChanged(OrderStatusChangedEvent event) {
        auditLogs.add("AUDIT: Order " + event.getOrder().getOrderId() + " transitioned from "
                + event.getPreviousStatus() + " to " + event.getNewStatus());
    }
}
