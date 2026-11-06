package com.restaurant.model;

import com.restaurant.decorator.PreparedItem;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {
    private final String id;
    private final PreparedItem preparedItem;
    private final int quantity;
    private final String specialInstructions;

    public OrderItem(String id, PreparedItem preparedItem, int quantity, String specialInstructions) {
        this.id = Objects.requireNonNull(id, "OrderItem ID cannot be null");
        this.preparedItem = Objects.requireNonNull(preparedItem, "PreparedItem cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
        this.specialInstructions = specialInstructions != null ? specialInstructions : "";
    }

    public String getId() {
        return id;
    }

    public PreparedItem getPreparedItem() {
        return preparedItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public BigDecimal getSubtotal() {
        return preparedItem.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}

