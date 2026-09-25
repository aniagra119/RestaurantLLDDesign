package com.restaurant.event.listener;

import com.restaurant.event.KitchenEvent;
import com.restaurant.event.KitchenEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuditLogListener implements KitchenEventListener {
    private final List<KitchenEvent> eventLog = new ArrayList<>();

    @Override
    public synchronized void onEvent(KitchenEvent event) {
        eventLog.add(event);
    }

    public synchronized List<KitchenEvent> getEventLog() {
        return Collections.unmodifiableList(eventLog);
    }

    public synchronized int getEventCount() {
        return eventLog.size();
    }

    public synchronized void clear() {
        eventLog.clear();
    }
}

