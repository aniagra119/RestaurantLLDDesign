package com.restaurant.model;

import com.restaurant.enums.DietaryType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Menu {
    private final String id;
    private final String name;
    private final List<MenuCourse> menuCourses;

    public Menu(String id, String name) {
        this.id = Objects.requireNonNull(id, "Menu ID cannot be null");
        this.name = Objects.requireNonNull(name, "Menu name cannot be null");
        this.menuCourses = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MenuCourse> getMenuCourses() {
        return Collections.unmodifiableList(menuCourses);
    }

    public void addMenuCourse(MenuCourse menuCourse) {
        Objects.requireNonNull(menuCourse, "MenuCourse cannot be null");
        this.menuCourses.add(menuCourse);
    }

    public Optional<MenuItem> findMenuItemById(String menuItemId) {
        for (MenuCourse course : menuCourses) {
            for (MenuItem item : course.getItems()) {
                if (item.getId().equals(menuItemId)) {
                    return Optional.of(item);
                }
            }
        }
        return Optional.empty();
    }

    public List<MenuItem> filterByDietary(DietaryType dietaryType) {
        List<MenuItem> result = new ArrayList<>();
        for (MenuCourse course : menuCourses) {
            if (!course.isActive()) continue;
            for (MenuItem item : course.getItems()) {
                if (item.isAvailable() && item.getDish().getDietaryType() == dietaryType) {
                    result.add(item);
                }
            }
        }
        return result;
    }
}

