package com.restaurant.model;

import com.restaurant.enums.ChefLevel;
import com.restaurant.enums.KitchenStation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Chef extends Employee {
    private final ChefLevel level;
    private final KitchenStation primaryStation;
    private final List<Order> currentOrders;
    private final int maxConcurrentOrders;

    public Chef(String id, String name, String email, String phoneNumber, ChefLevel level) {
        this(id, name, email, phoneNumber, level, KitchenStation.HOT_KITCHEN);
    }

    public Chef(String id, String name, String email, String phoneNumber, ChefLevel level,
            KitchenStation primaryStation) {
        super(id, name, email, phoneNumber);
        this.level = Objects.requireNonNull(level, "Chef level cannot be null");
        this.primaryStation = primaryStation != null ? primaryStation : KitchenStation.HOT_KITCHEN;
        this.currentOrders = new ArrayList<>();
        this.maxConcurrentOrders = determineMaxOrders(level);
    }

    private static int determineMaxOrders(ChefLevel level) {
        return switch (level) {
            case COMMIS_CHEF -> 2;
            case SOUS_CHEF -> 4;
            case HEAD_CHEF -> 6;
            case EXECUTIVE_CHEF -> 3; // Executive chefs balance cooking with administrative kitchen oversight
        };
    }

    public ChefLevel getLevel() {
        return level;
    }

    public KitchenStation getPrimaryStation() {
        return primaryStation;
    }

    public int getMaxConcurrentOrders() {
        return maxConcurrentOrders;
    }

    public synchronized boolean canAcceptOrder() {
        return currentOrders.size() < maxConcurrentOrders;
    }

    public synchronized void acceptOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        if (!canAcceptOrder()) {
            throw new IllegalStateException("Chef " + getName() + " is at maximum capacity (" + maxConcurrentOrders + " orders)");
        }
        currentOrders.add(order);
        order.assignToChef(this);
    }

    public synchronized void completeOrder(Order order) {
        currentOrders.remove(order);
    }

    public synchronized List<Order> getCurrentOrders() {
        return Collections.unmodifiableList(currentOrders);
    }
}
