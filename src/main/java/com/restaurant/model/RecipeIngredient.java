package com.restaurant.model;

import com.restaurant.enums.MeasurementUnit;

import java.util.Objects;

public class RecipeIngredient {
    private final Ingredient ingredient;
    private final double amount;
    private final MeasurementUnit unit;

    public RecipeIngredient(Ingredient ingredient, double amount, MeasurementUnit unit) {
        this.ingredient = Objects.requireNonNull(ingredient, "Ingredient cannot be null");
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.amount = amount;
        this.unit = Objects.requireNonNull(unit, "Measurement unit cannot be null");

        if (ingredient.getUnit() != unit) {
            throw new IllegalArgumentException(String.format(
                    "Unit mismatch: Ingredient %s uses %s, but recipe requested %s",
                    ingredient.getName(), ingredient.getUnit(), unit));
        }
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getAmount() {
        return amount;
    }

    public MeasurementUnit getUnit() {
        return unit;
    }
}
