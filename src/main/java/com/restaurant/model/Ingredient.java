package com.restaurant.model;

import com.restaurant.enums.Unit;

import java.util.Objects;

public class Ingredient {
    private final String id;
    private final String name;
    private final Unit baseUnit;
    private double availableQuantity;

    public Ingredient(String id, String name, Unit baseUnit, double initialQuantity) {
        this.id = Objects.requireNonNull(id, "Ingredient ID must not be null");
        this.name = Objects.requireNonNull(name, "Ingredient name must not be null");
        this.baseUnit = Objects.requireNonNull(baseUnit, "Base unit must not be null");
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("Initial quantity cannot be negative");
        }
        this.availableQuantity = initialQuantity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Unit getBaseUnit() {
        return baseUnit;
    }

    public synchronized double getAvailableQuantity() {
        return availableQuantity;
    }

    public synchronized void restock(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Restock amount must be positive");
        }
        this.availableQuantity += amount;
    }

    public synchronized boolean hasEnoughStock(double requiredBaseAmount) {
        return this.availableQuantity >= requiredBaseAmount;
    }

    public synchronized void deductStock(double amountInBaseUnit) {
        if (amountInBaseUnit < 0) {
            throw new IllegalArgumentException("Deduct amount cannot be negative");
        }
        if (this.availableQuantity < amountInBaseUnit) {
            throw new IllegalStateException("Insufficient inventory for ingredient: " + name +
                    " (Required: " + amountInBaseUnit + " " + baseUnit.getSymbol() +
                    ", Available: " + availableQuantity + " " + baseUnit.getSymbol() + ")");
        }
        this.availableQuantity -= amountInBaseUnit;
    }
}

