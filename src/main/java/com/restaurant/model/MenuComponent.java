package com.restaurant.model;

import java.util.List;

public interface MenuComponent {
    String getName();
    List<Dish> getAllDishes();
    void display(int indentLevel);
}
