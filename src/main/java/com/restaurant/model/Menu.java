package com.restaurant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Menu implements MenuComponent {
    private final String menuId;
    private final String name;
    private final List<MenuSection> sections;

    public Menu(String menuId, String name) {
        this(menuId, name, new ArrayList<>());
    }

    public Menu(String menuId, String name, List<MenuSection> sections) {
        this.menuId = Objects.requireNonNull(menuId, "Menu ID cannot be null");
        this.name = Objects.requireNonNull(name, "Menu name cannot be null");
        this.sections = new ArrayList<>(Objects.requireNonNull(sections, "Sections cannot be null"));
    }

    public String getMenuId() {
        return menuId;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<MenuSection> getSections() {
        return Collections.unmodifiableList(sections);
    }

    @Override
    public List<Dish> getAllDishes() {
        return sections.stream()
                .flatMap(s -> s.getDishes().stream())
                .collect(Collectors.toList());
    }

    public void addSection(MenuSection section) {
        Objects.requireNonNull(section, "Menu section cannot be null");
        sections.add(section);
    }

    public Optional<Dish> findDishByName(String dishName) {
        return sections.stream()
                .flatMap(s -> s.getDishes().stream())
                .filter(d -> d.getName().equalsIgnoreCase(dishName))
                .findFirst();
    }

    public Optional<Dish> findDishById(String dishId) {
        return sections.stream()
                .flatMap(s -> s.getDishes().stream())
                .filter(d -> d.getId().equals(dishId))
                .findFirst();
    }

    @Override
    public void display(int indentLevel) {
        String indent = "  ".repeat(indentLevel);
        System.out.println(indent + "Menu: " + name);
        for (MenuSection section : sections) {
            section.display(indentLevel + 1);
        }
    }
}
