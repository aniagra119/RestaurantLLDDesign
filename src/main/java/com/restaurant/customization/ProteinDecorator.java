package com.restaurant.customization;

import com.restaurant.enums.DietaryType;

import java.util.Objects;

public class ProteinDecorator extends DishCustomizationDecorator {
    private final String proteinName;
    private final double additionalCost;

    public ProteinDecorator(OrderableItem wrappedItem, String proteinName, double additionalCost) {
        super(wrappedItem);
        this.proteinName = Objects.requireNonNull(proteinName, "Protein name cannot be null");
        if (additionalCost < 0) {
            throw new IllegalArgumentException("Additional cost cannot be negative");
        }
        this.additionalCost = additionalCost;
    }

    @Override
    public String getName() {
        return wrappedItem.getName() + " with " + proteinName;
    }

    @Override
    public double getPrice() {
        return wrappedItem.getPrice() + additionalCost;
    }

    @Override
    public DietaryType getDietaryType() {
        return DietaryType.NON_VEG;
    }

    public String getProteinName() {
        return proteinName;
    }

    public double getAdditionalCost() {
        return additionalCost;
    }
}
