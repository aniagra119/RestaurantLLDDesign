package com.restaurant.model;

import java.math.BigDecimal;
import java.util.Objects;

public class MenuItem {
    private final String id;
    private final Dish dish;
    private BigDecimal price;
    private boolean available;

    public MenuItem(String id, Dish dish, BigDecimal price) {
        this.id = Objects.requireNonNull(id, "MenuItem ID cannot be null");
        this.dish = Objects.requireNonNull(dish, "Dish cannot be null");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative or null");
        }
        this.price = price;
        this.available = true;
    }

    public String getId() {
        return id;
    }

    public Dish getDish() {
        return dish;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative or null");
        }
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

