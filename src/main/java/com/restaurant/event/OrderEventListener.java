package com.restaurant.event;

public interface OrderEventListener {
    void onOrderPlaced(OrderPlacedEvent event);
    void onOrderStatusChanged(OrderStatusChangedEvent event);
}
