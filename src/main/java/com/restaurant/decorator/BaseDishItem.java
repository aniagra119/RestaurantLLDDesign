package com.restaurant.decorator;

import com.restaurant.enums.DietaryType;
import com.restaurant.model.Dish;
import com.restaurant.model.MenuItem;
import com.restaurant.model.RecipeIngredient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class BaseDishItem implements PreparedItem {
    private final MenuItem menuItem;

    public BaseDishItem(MenuItem menuItem) {
        this.menuItem = Objects.requireNonNull(menuItem, "MenuItem cannot be null");
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    @Override
    public Dish getRootDish() {
        return menuItem.getDish();
    }

    @Override
    public String getName() {
        return menuItem.getDish().getName();
    }

    @Override
    public BigDecimal getPrice() {
        return menuItem.getPrice();
    }

    @Override
    public DietaryType getDietaryType() {
        return menuItem.getDish().getDietaryType();
    }

    @Override
    public List<RecipeIngredient> getIngredients() {
        return menuItem.getDish().getRecipe().getIngredients();
    }

    @Override
    public List<String> getCookingInstructions() {
        return menuItem.getDish().getRecipe().getInstructions();
    }
}
