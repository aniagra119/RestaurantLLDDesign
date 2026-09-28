package com.restaurant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Recipe {
    private final String id;
    private final List<String> instructions;
    private final int prepTimeMinutes;
    private final List<RecipeIngredient> ingredients;

    public Recipe(String id, List<String> instructions, int prepTimeMinutes, List<RecipeIngredient> ingredients) {
        this.id = Objects.requireNonNull(id, "Recipe ID cannot be null");
        this.instructions = new ArrayList<>(Objects.requireNonNull(instructions, "Instructions cannot be null"));
        this.prepTimeMinutes = Math.max(0, prepTimeMinutes);
        this.ingredients = new ArrayList<>(Objects.requireNonNull(ingredients, "Ingredients cannot be null"));
    }

    public String getId() {
        return id;
    }

    public List<String> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public List<RecipeIngredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe recipe)) return false;
        return Objects.equals(id, recipe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
