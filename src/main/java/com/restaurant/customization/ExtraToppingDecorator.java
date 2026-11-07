package com.restaurant.customization;

import java.util.Objects;

public class ExtraToppingDecorator extends DishCustomizationDecorator {
    private final String toppingName;
    private final double additionalCost;

    public ExtraToppingDecorator(OrderableItem wrappedItem, String toppingName, double additionalCost) {
        super(wrappedItem);
        this.toppingName = Objects.requireNonNull(toppingName, "Topping name cannot be null");
        if (additionalCost < 0) {
            throw new IllegalArgumentException("Additional cost cannot be negative");
        }
        this.additionalCost = additionalCost;
    }

    @Override
    public String getName() {
        return wrappedItem.getName() + " + " + toppingName;
    }

    @Override
    public double getPrice() {
        return wrappedItem.getPrice() + additionalCost;
    }

    public String getToppingName() {
        return toppingName;
    }

    public double getAdditionalCost() {
        return additionalCost;
    }
}

