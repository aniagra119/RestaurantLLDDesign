package com.restaurant.event.listener;

import com.restaurant.event.OrderEventListener;
import com.restaurant.event.OrderPlacedEvent;
import com.restaurant.event.OrderStatusChangedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerNotificationListener implements OrderEventListener {
    private final List<String> notifications = new ArrayList<>();

    public synchronized List<String> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    @Override
    public synchronized void onOrderPlaced(OrderPlacedEvent event) {
        notifications.add(String.format("Notification to %s: Order %s placed for table %s",
                event.getVisit().getCustomer().getName(),
                event.getOrder().getOrderId(),
                event.getVisit().getTable().getTableNumber()));
    }

    @Override
    public synchronized void onOrderStatusChanged(OrderStatusChangedEvent event) {
        notifications.add(String.format("Order %s updated to %s",
                event.getOrder().getOrderId(),
                event.getNewStatus()));
    }
}
