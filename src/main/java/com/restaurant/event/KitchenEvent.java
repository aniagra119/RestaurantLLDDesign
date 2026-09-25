package com.restaurant.event;

import java.time.LocalDateTime;

public interface KitchenEvent {
    String getEventId();
    LocalDateTime getTimestamp();
    String getEventType();
}

