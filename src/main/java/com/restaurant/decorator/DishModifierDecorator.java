package com.restaurant.decorator;

import com.restaurant.enums.DietaryType;
import com.restaurant.model.Dish;
import com.restaurant.model.RecipeIngredient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public abstract class DishModifierDecorator implements PreparedItem {
    protected final PreparedItem wrappedItem;

    protected DishModifierDecorator(PreparedItem wrappedItem) {
        this.wrappedItem = Objects.requireNonNull(wrappedItem, "Wrapped PreparedItem cannot be null");
    }

    @Override
    public Dish getRootDish() {
        return wrappedItem.getRootDish();
    }

    @Override
    public String getName() {
        return wrappedItem.getName();
    }

    @Override
    public BigDecimal getPrice() {
        return wrappedItem.getPrice();
    }

    @Override
    public DietaryType getDietaryType() {
        return wrappedItem.getDietaryType();
    }

    @Override
    public List<RecipeIngredient> getIngredients() {
        return wrappedItem.getIngredients();
    }

    @Override
    public List<String> getCookingInstructions() {
        return wrappedItem.getCookingInstructions();
    }
}
