package com.restaurant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MenuSection {
    private final String sectionName;
    private final List<Dish> dishes;

    public MenuSection(String sectionName) {
        this(sectionName, new ArrayList<>());
    }

    public MenuSection(String sectionName, List<Dish> dishes) {
        this.sectionName = Objects.requireNonNull(sectionName, "Section name cannot be null");
        this.dishes = new ArrayList<>(Objects.requireNonNull(dishes, "Dishes list cannot be null"));
    }

    public String getSectionName() {
        return sectionName;
    }

    public List<Dish> getDishes() {
        return Collections.unmodifiableList(dishes);
    }

    public void addDish(Dish dish) {
        Objects.requireNonNull(dish, "Dish cannot be null");
        dishes.add(dish);
    }

    public Optional<Dish> findDishByName(String name) {
        return dishes.stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
