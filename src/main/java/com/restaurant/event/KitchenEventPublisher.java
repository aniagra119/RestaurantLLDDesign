package com.restaurant.event;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class KitchenEventPublisher {
    private final List<KitchenEventListener> listeners = new CopyOnWriteArrayList<>();

    public void registerListener(KitchenEventListener listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        listeners.add(listener);
    }

    public void unregisterListener(KitchenEventListener listener) {
        listeners.remove(listener);
    }

    public void publish(KitchenEvent event) {
        Objects.requireNonNull(event, "Event cannot be null");
        for (KitchenEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public int getListenerCount() {
        return listeners.size();
    }
}

