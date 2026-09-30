package com.restaurant.model;

import com.restaurant.enums.IngredientType;
import com.restaurant.enums.MeasurementUnit;
import com.restaurant.exception.InsufficientInventoryException;

import java.util.Objects;

public class Ingredient {
    private final String id;
    private final String name;
    private final IngredientType type;
    private final MeasurementUnit unit;
    private double currentStock;

    public Ingredient(String id, String name, IngredientType type, MeasurementUnit unit, double initialStock) {
        this.id = Objects.requireNonNull(id, "Ingredient ID cannot be null");
        this.name = Objects.requireNonNull(name, "Ingredient name cannot be null");
        this.type = Objects.requireNonNull(type, "Ingredient type cannot be null");
        this.unit = Objects.requireNonNull(unit, "Measurement unit cannot be null");

        // Validate solid vs liquid consistency
        if (type == IngredientType.SOLID && unit != MeasurementUnit.GRAMS) {
            throw new IllegalArgumentException("Solid ingredients must be measured in GRAMS");
        }
        if (type == IngredientType.LIQUID && unit != MeasurementUnit.MILLILITERS) {
            throw new IllegalArgumentException("Liquid ingredients must be measured in MILLILITERS");
        }
        if (initialStock < 0) {
            throw new IllegalArgumentException("Initial stock cannot be negative");
        }
        this.currentStock = initialStock;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public IngredientType getType() {
        return type;
    }

    public MeasurementUnit getUnit() {
        return unit;
    }

    public synchronized double getCurrentStock() {
        return currentStock;
    }

    public synchronized void addStock(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Stock to add must be positive");
        }
        this.currentStock += amount;
    }

    public synchronized void deductStock(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Stock to deduct must be positive");
        }
        if (currentStock < amount) {
            throw new InsufficientInventoryException(String.format(
                    "Insufficient stock for %s (%s). Required: %.2f %s, Available: %.2f %s",
                    name, id, amount, unit, currentStock, unit));
        }
        this.currentStock -= amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Ingredient that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
