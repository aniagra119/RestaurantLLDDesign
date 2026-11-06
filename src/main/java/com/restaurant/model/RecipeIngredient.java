package com.restaurant.model;

import com.restaurant.enums.Unit;

import java.util.Objects;

public class RecipeIngredient {
    private final Ingredient ingredient;
    private final double amount;
    private final Unit unit;

    public RecipeIngredient(Ingredient ingredient, double amount, Unit unit) {
        this.ingredient = Objects.requireNonNull(ingredient, "Ingredient cannot be null");
        this.unit = Objects.requireNonNull(unit, "Unit cannot be null");
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!ingredient.getBaseUnit().isCompatibleWith(unit)) {
            throw new IllegalArgumentException("Unit " + unit + " is dimensionally incompatible with ingredient base unit " + ingredient.getBaseUnit());
        }
        this.amount = amount;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getAmount() {
        return amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public double getBaseAmount() {
        return amount * unit.getConversionFactorToBase();
    }
}

