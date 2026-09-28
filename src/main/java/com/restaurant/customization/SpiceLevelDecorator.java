package com.restaurant.customization;

import java.util.Objects;

public class SpiceLevelDecorator extends DishCustomizationDecorator {
    private final String spiceLevel;

    public SpiceLevelDecorator(OrderableItem wrappedItem, String spiceLevel) {
        super(wrappedItem);
        this.spiceLevel = Objects.requireNonNull(spiceLevel, "Spice level cannot be null");
    }

    @Override
    public String getName() {
        return wrappedItem.getName() + " (" + spiceLevel + ")";
    }

    public String getSpiceLevel() {
        return spiceLevel;
    }
}
