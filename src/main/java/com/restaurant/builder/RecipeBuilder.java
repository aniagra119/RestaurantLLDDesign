package com.restaurant.builder;

import com.restaurant.enums.MeasurementUnit;
import com.restaurant.model.Ingredient;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeBuilder {
    private String id;
    private final List<String> instructions = new ArrayList<>();
    private int prepTimeMinutes = 15;
    private final List<RecipeIngredient> ingredients = new ArrayList<>();

    public RecipeBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public RecipeBuilder addInstruction(String instruction) {
        this.instructions.add(instruction);
        return this;
    }

    public RecipeBuilder setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
        return this;
    }

    public RecipeBuilder addIngredient(Ingredient ingredient, double amount, MeasurementUnit unit) {
        this.ingredients.add(new RecipeIngredient(ingredient, amount, unit));
        return this;
    }

    public Recipe build() {
        if (id == null) {
            id = "RECIPE-" + System.currentTimeMillis();
        }
        return new Recipe(id, instructions, prepTimeMinutes, ingredients);
    }
}
