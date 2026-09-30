package com.restaurant.model;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class BaseEntity {
    protected final String id;
    protected final LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public BaseEntity(String id) {
        this.id = Objects.requireNonNull(id, "Entity ID cannot be null");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    protected void markUpdated() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
