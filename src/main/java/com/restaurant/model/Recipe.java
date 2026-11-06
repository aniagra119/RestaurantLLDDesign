package com.restaurant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Recipe {
    private final String id;
    private final String title;
    private final List<String> instructions;
    private final int prepTimeMinutes;
    private final int cookTimeMinutes;
    private final List<RecipeIngredient> ingredients;

    public Recipe(String id, String title, List<String> instructions, int prepTimeMinutes, int cookTimeMinutes, List<RecipeIngredient> ingredients) {
        this.id = Objects.requireNonNull(id, "Recipe ID cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.instructions = instructions != null ? new ArrayList<>(instructions) : new ArrayList<>();
        this.prepTimeMinutes = prepTimeMinutes;
        this.cookTimeMinutes = cookTimeMinutes;
        this.ingredients = ingredients != null ? new ArrayList<>(ingredients) : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public int getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public int getTotalTimeMinutes() {
        return prepTimeMinutes + cookTimeMinutes;
    }

    public List<RecipeIngredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }
}

