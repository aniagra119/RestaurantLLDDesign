package com.restaurant.customization;

import com.restaurant.enums.DietaryType;

public interface OrderableItem {
    String getName();
    double getPrice();
    DietaryType getDietaryType();
}
