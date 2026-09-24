package com.restaurant.decorator;

import com.restaurant.model.RecipeIngredient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddOnDecorator extends DishModifierDecorator {
    private final String addOnName;
    private final BigDecimal additionalPrice;
    private final RecipeIngredient addOnIngredient;

    public AddOnDecorator(PreparedItem wrappedItem, String addOnName, BigDecimal additionalPrice, RecipeIngredient addOnIngredient) {
        super(wrappedItem);
        this.addOnName = Objects.requireNonNull(addOnName, "AddOn name cannot be null");
        this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
        this.addOnIngredient = addOnIngredient;
    }

    @Override
    public String getName() {
        return wrappedItem.getName() + " (+ Extra " + addOnName + ")";
    }

    @Override
    public BigDecimal getPrice() {
        return wrappedItem.getPrice().add(additionalPrice);
    }

    @Override
    public List<RecipeIngredient> getIngredients() {
        List<RecipeIngredient> combined = new ArrayList<>(wrappedItem.getIngredients());
        if (addOnIngredient != null) {
            combined.add(addOnIngredient);
        }
        return combined;
    }

    @Override
    public List<String> getCookingInstructions() {
        List<String> combined = new ArrayList<>(wrappedItem.getCookingInstructions());
        combined.add("Top with generous portion of " + addOnName + ".");
        return combined;
    }
}

