package com.restaurant.event.listener;

import com.restaurant.enums.KitchenStation;
import com.restaurant.event.OrderEventListener;
import com.restaurant.event.OrderPlacedEvent;
import com.restaurant.event.OrderStatusChangedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class KitchenDisplayListener implements OrderEventListener {
    private final KitchenStation station;
    private final List<String> routedOrders = new ArrayList<>();

    public KitchenDisplayListener(KitchenStation station) {
        this.station = Objects.requireNonNull(station, "Kitchen station cannot be null");
    }

    public KitchenStation getStation() {
        return station;
    }

    public synchronized List<String> getRoutedOrders() {
        return Collections.unmodifiableList(routedOrders);
    }

    @Override
    public synchronized void onOrderPlaced(OrderPlacedEvent event) {
        routedOrders.add(event.getOrder().getOrderId());
    }

    @Override
    public void onOrderStatusChanged(OrderStatusChangedEvent event) {
        // No-op or status update
    }
}

