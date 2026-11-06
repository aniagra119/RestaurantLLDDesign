package com.restaurant.builder;

import com.restaurant.enums.Unit;
import com.restaurant.model.Ingredient;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RecipeBuilder {
    private final String id;
    private final String title;
    private int prepTimeMinutes = 10;
    private int cookTimeMinutes = 15;
    private final List<String> instructions = new ArrayList<>();
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public RecipeBuilder(String title) {
        this("REC-" + UUID.randomUUID().toString().substring(0, 8), title);
    }

    public RecipeBuilder(String id, String title) {
        this.id = Objects.requireNonNull(id, "Recipe ID cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    public static RecipeBuilder create(String title) {
        return new RecipeBuilder(title);
    }

    public RecipeBuilder prepTime(int minutes) {
        this.prepTimeMinutes = minutes;
        return this;
    }

    public RecipeBuilder cookTime(int minutes) {
        this.cookTimeMinutes = minutes;
        return this;
    }

    public RecipeBuilder addInstruction(String step) {
        this.instructions.add(step);
        return this;
    }

    public RecipeBuilder addIngredient(Ingredient ingredient, double amount, Unit unit) {
        this.ingredients.add(new RecipeIngredient(ingredient, amount, unit));
        return this;
    }

    public Recipe build() {
        return new Recipe(id, title, instructions, prepTimeMinutes, cookTimeMinutes, ingredients);
    }
}

