package com.restaurant.model;

import com.restaurant.enums.CuisineType;

import java.util.Objects;

public class Cuisine {
    private final String id;
    private final String name;
    private final CuisineType type;
    private boolean active;

    public Cuisine(String id, String name, CuisineType type) {
        this.id = Objects.requireNonNull(id, "Cuisine ID cannot be null");
        this.name = Objects.requireNonNull(name, "Cuisine name cannot be null");
        this.type = Objects.requireNonNull(type, "Cuisine type cannot be null");
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CuisineType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

