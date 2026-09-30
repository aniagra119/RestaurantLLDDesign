package com.restaurant.customization;

import com.restaurant.enums.DietaryType;

import java.util.Objects;

public abstract class DishCustomizationDecorator implements OrderableItem {
    protected final OrderableItem wrappedItem;

    public DishCustomizationDecorator(OrderableItem wrappedItem) {
        this.wrappedItem = Objects.requireNonNull(wrappedItem, "Wrapped item cannot be null");
    }

    @Override
    public String getName() {
        return wrappedItem.getName();
    }

    @Override
    public double getPrice() {
        return wrappedItem.getPrice();
    }

    @Override
    public DietaryType getDietaryType() {
        return wrappedItem.getDietaryType();
    }

    public OrderableItem getWrappedItem() {
        return wrappedItem;
    }
}

