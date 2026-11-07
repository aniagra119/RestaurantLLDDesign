package com.restaurant.model;

import com.restaurant.enums.DietaryType;
import com.restaurant.enums.KitchenStation;

import java.util.Objects;

public class Dish {
    private final String id;
    private final String name;
    private final String description;
    private final DietaryType type;
    private final double price;
    private final Recipe recipe;
    private final KitchenStation targetStation;
    private boolean isAvailable;

    public Dish(String id, String name, String description, DietaryType type, double price, Recipe recipe) {
        this(id, name, description, type, price, recipe, KitchenStation.HOT_KITCHEN);
    }

    public Dish(String id, String name, String description, DietaryType type, double price, Recipe recipe,
            KitchenStation targetStation) {
        this.id = Objects.requireNonNull(id, "Dish ID cannot be null");
        this.name = Objects.requireNonNull(name, "Dish name cannot be null");
        this.description = description;
        this.type = Objects.requireNonNull(type, "Dietary type cannot be null");
        if (price < 0) {
            throw new IllegalArgumentException("Dish price cannot be negative");
        }
        this.price = price;
        this.recipe = Objects.requireNonNull(recipe, "Recipe cannot be null");
        this.targetStation = targetStation != null ? targetStation : KitchenStation.HOT_KITCHEN;
        this.isAvailable = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public DietaryType getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public KitchenStation getTargetStation() {
        return targetStation;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Dish dish))
            return false;

        return Objects.equals(id, dish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
