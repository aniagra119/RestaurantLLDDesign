package com.restaurant.decorator;

import com.restaurant.enums.DietaryType;
import com.restaurant.model.Dish;
import com.restaurant.model.RecipeIngredient;

import java.math.BigDecimal;
import java.util.List;

public interface PreparedItem {
    String getName();
    BigDecimal getPrice();
    DietaryType getDietaryType();
    List<RecipeIngredient> getIngredients();
    List<String> getCookingInstructions();
    Dish getRootDish();
}
