package com.restaurant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MenuCourse {
    private final String id;
    private final Course course;
    private boolean active;
    private int displayOrder;
    private final List<MenuItem> items;

    public MenuCourse(String id, Course course, int displayOrder) {
        this.id = Objects.requireNonNull(id, "MenuCourse ID cannot be null");
        this.course = Objects.requireNonNull(course, "Course cannot be null");
        this.displayOrder = displayOrder;
        this.active = true;
        this.items = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(MenuItem item) {
        Objects.requireNonNull(item, "MenuItem cannot be null");
        this.items.add(item);
    }

    public void removeItem(String menuItemId) {
        this.items.removeIf(item -> item.getId().equals(menuItemId));
    }
}

