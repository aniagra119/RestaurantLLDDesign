package com.restaurant.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OrderEventPublisher {
    private final List<OrderEventListener> listeners = new ArrayList<>();

    public synchronized void subscribe(OrderEventListener listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void unsubscribe(OrderEventListener listener) {
        listeners.remove(listener);
    }

    public synchronized List<OrderEventListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    public synchronized void publishOrderPlaced(OrderPlacedEvent event) {
        for (OrderEventListener listener : listeners) {
            listener.onOrderPlaced(event);
        }
    }

    public synchronized void publishStatusChanged(OrderStatusChangedEvent event) {
        for (OrderEventListener listener : listeners) {
            listener.onOrderStatusChanged(event);
        }
    }
}
