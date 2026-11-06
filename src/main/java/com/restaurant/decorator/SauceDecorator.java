package com.restaurant.decorator;

import com.restaurant.model.RecipeIngredient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SauceDecorator extends DishModifierDecorator {
    private final String sauceName;
    private final BigDecimal additionalPrice;
    private final RecipeIngredient sauceIngredient;

    public SauceDecorator(PreparedItem wrappedItem, String sauceName, BigDecimal additionalPrice, RecipeIngredient sauceIngredient) {
        super(wrappedItem);
        this.sauceName = Objects.requireNonNull(sauceName, "Sauce name cannot be null");
        this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
        this.sauceIngredient = Objects.requireNonNull(sauceIngredient, "Sauce ingredient cannot be null");
    }

    @Override
    public String getName() {
        return wrappedItem.getName() + " in " + sauceName + " Sauce";
    }

    @Override
    public BigDecimal getPrice() {
        return wrappedItem.getPrice().add(additionalPrice);
    }

    @Override
    public List<RecipeIngredient> getIngredients() {
        List<RecipeIngredient> combined = new ArrayList<>(wrappedItem.getIngredients());
        combined.add(sauceIngredient);
        return combined;
    }

    @Override
    public List<String> getCookingInstructions() {
        List<String> combined = new ArrayList<>(wrappedItem.getCookingInstructions());
        combined.add("Toss with " + sauceName + " sauce and simmer for 3 minutes.");
        return combined;
    }
}

