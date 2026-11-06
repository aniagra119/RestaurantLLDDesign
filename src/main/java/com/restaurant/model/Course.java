package com.restaurant.model;

import java.util.Objects;

public class Course {
    private final String id;
    private final String name;

    public Course(String id, String name) {
        this.id = Objects.requireNonNull(id, "Course ID cannot be null");
        this.name = Objects.requireNonNull(name, "Course name cannot be null");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course course)) return false;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

