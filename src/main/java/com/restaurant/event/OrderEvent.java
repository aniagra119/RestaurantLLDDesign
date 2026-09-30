package com.restaurant.event;

import java.time.Instant;
import java.util.Objects;

public abstract class OrderEvent {
    private final String eventId;
    private final long timestamp;

    public OrderEvent(String eventId) {
        this.eventId = Objects.requireNonNull(eventId, "Event ID cannot be null");
        this.timestamp = Instant.now().toEpochMilli();
    }

    public String getEventId() {
        return eventId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

