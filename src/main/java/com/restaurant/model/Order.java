package com.restaurant.model;

import com.restaurant.enums.OrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order extends BaseEntity {
    private final List<OrderItem> items;
    private OrderStatus status;
    private final long placedTimestamp;
    private Chef assignedChef;

    public Order(String orderId) {
        super(orderId);
        this.items = new ArrayList<>();
        this.status = OrderStatus.PLACED;
        this.placedTimestamp = Instant.now().toEpochMilli();
    }

    public String getOrderId() {
        return getId();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public synchronized OrderStatus getStatus() {
        return status;
    }

    public long getPlacedTimestamp() {
        return placedTimestamp;
    }

    public synchronized Chef getAssignedChef() {
        return assignedChef;
    }

    public synchronized void assignToChef(Chef chef) {
        Objects.requireNonNull(chef, "Chef cannot be null");
        this.assignedChef = chef;
        markUpdated();
    }

    public synchronized void addItem(OrderItem item) {
        Objects.requireNonNull(item, "Order item cannot be null");
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException("Cannot add items to an order that is already " + status);
        }
        items.add(item);
        markUpdated();
    }

    public synchronized void updateStatus(OrderStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "Order status cannot be null");
        markUpdated();
    }

    public double calculateSubtotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }
}
