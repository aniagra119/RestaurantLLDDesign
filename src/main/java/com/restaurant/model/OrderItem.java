package com.restaurant.model;

import com.restaurant.customization.OrderableItem;

import java.util.Objects;

public class OrderItem {
    private final String itemId;
    private final OrderableItem item;
    private final int quantity;
    private final String specialInstructions;

    public OrderItem(String itemId, OrderableItem item, int quantity) {
        this(itemId, item, quantity, "");
    }

    public OrderItem(String itemId, OrderableItem item, int quantity, String specialInstructions) {
        this.itemId = Objects.requireNonNull(itemId, "Item ID cannot be null");
        this.item = Objects.requireNonNull(item, "Orderable item cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
        this.specialInstructions = specialInstructions != null ? specialInstructions : "";
    }

    public String getItemId() {
        return itemId;
    }

    public OrderableItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public double getSubtotal() {
        return item.getPrice() * quantity;
    }
}
