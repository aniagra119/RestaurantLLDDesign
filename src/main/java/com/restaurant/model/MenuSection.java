package com.restaurant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MenuSection implements MenuComponent {
    private final String sectionName;
    private final List<Dish> dishes;

    public MenuSection(String sectionName) {
        this(sectionName, new ArrayList<>());
    }

    public MenuSection(String sectionName, List<Dish> dishes) {
        this.sectionName = Objects.requireNonNull(sectionName, "Section name cannot be null");
        this.dishes = new ArrayList<>(Objects.requireNonNull(dishes, "Dishes list cannot be null"));
    }

    @Override
    public String getName() {
        return sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public List<Dish> getDishes() {
        return Collections.unmodifiableList(dishes);
    }

    @Override
    public List<Dish> getAllDishes() {
        return getDishes();
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

    @Override
    public void display(int indentLevel) {
        String indent = "  ".repeat(indentLevel);
        System.out.println(indent + "Section: " + sectionName);
        for (Dish dish : dishes) {
            System.out.println(indent + "  - " + dish.getName() + " (₹" + dish.getPrice() + ")");
        }
    }
}
