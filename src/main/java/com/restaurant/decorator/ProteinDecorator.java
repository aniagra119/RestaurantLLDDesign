package com.restaurant.decorator;

import com.restaurant.enums.DietaryType;
import com.restaurant.model.RecipeIngredient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProteinDecorator extends DishModifierDecorator {
    private final String proteinName;
    private final BigDecimal additionalPrice;
    private final DietaryType proteinDietaryType;
    private final RecipeIngredient proteinIngredient;

    public ProteinDecorator(PreparedItem wrappedItem, String proteinName, BigDecimal additionalPrice,
                            DietaryType proteinDietaryType, RecipeIngredient proteinIngredient) {
        super(wrappedItem);
        this.proteinName = Objects.requireNonNull(proteinName, "Protein name cannot be null");
        this.additionalPrice = additionalPrice != null ? additionalPrice : BigDecimal.ZERO;
        this.proteinDietaryType = Objects.requireNonNull(proteinDietaryType, "Protein dietary type cannot be null");
        this.proteinIngredient = Objects.requireNonNull(proteinIngredient, "Protein ingredient cannot be null");
    }

    @Override
    public String getName() {
        return wrappedItem.getName() + " with " + proteinName;
    }

    @Override
    public BigDecimal getPrice() {
        return wrappedItem.getPrice().add(additionalPrice);
    }

    @Override
    public DietaryType getDietaryType() {
        // If protein is NON_VEG, whole dish becomes NON_VEG
        if (proteinDietaryType == DietaryType.NON_VEG) {
            return DietaryType.NON_VEG;
        }
        return wrappedItem.getDietaryType();
    }

    @Override
    public List<RecipeIngredient> getIngredients() {
        List<RecipeIngredient> combined = new ArrayList<>(wrappedItem.getIngredients());
        combined.add(proteinIngredient);
        return combined;
    }

    @Override
    public List<String> getCookingInstructions() {
        List<String> combined = new ArrayList<>(wrappedItem.getCookingInstructions());
        combined.add("Sear and incorporate " + proteinName + " until thoroughly cooked.");
        return combined;
    }
}

