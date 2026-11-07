package com.restaurant.customization;

import com.restaurant.enums.DietaryType;
import com.restaurant.model.Dish;

import java.util.Objects;

public class BaseDishItem implements OrderableItem {
    private final Dish dish;

    public BaseDishItem(Dish dish) {
        this.dish = Objects.requireNonNull(dish, "Dish cannot be null");
    }

    @Override
    public String getName() {
        return dish.getName();
    }

    @Override
    public double getPrice() {
        return dish.getPrice();
    }

    @Override
    public DietaryType getDietaryType() {
        return dish.getType();
    }

    public Dish getDish() {
        return dish;
    }
}

