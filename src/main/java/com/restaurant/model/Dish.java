package com.restaurant.model;

import com.restaurant.enums.DietaryType;
import com.restaurant.enums.KitchenStation;

import java.util.Objects;

public class Dish {
    private final String id;
    private final String name;
    private final String description;
    private final DietaryType dietaryType;
    private final Cuisine cuisine;
    private final Recipe recipe;
    private final KitchenStation targetStation;

    public Dish(String id, String name, String description, DietaryType dietaryType, Cuisine cuisine, Recipe recipe) {
        this(id, name, description, dietaryType, cuisine, recipe, KitchenStation.HOT_KITCHEN);
    }

    public Dish(String id, String name, String description, DietaryType dietaryType, Cuisine cuisine, Recipe recipe, KitchenStation targetStation) {
        this.id = Objects.requireNonNull(id, "Dish ID cannot be null");
        this.name = Objects.requireNonNull(name, "Dish name cannot be null");
        this.description = description;
        this.dietaryType = Objects.requireNonNull(dietaryType, "Dietary type cannot be null");
        this.cuisine = Objects.requireNonNull(cuisine, "Cuisine cannot be null");
        this.recipe = Objects.requireNonNull(recipe, "Recipe cannot be null");
        this.targetStation = targetStation != null ? targetStation : KitchenStation.HOT_KITCHEN;
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

    public DietaryType getDietaryType() {
        return dietaryType;
    }

    public Cuisine getCuisine() {
        return cuisine;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public KitchenStation getTargetStation() {
        return targetStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dish dish)) return false;
        return Objects.equals(id, dish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
