package com.restaurant.model;

import java.util.Objects;

public class Table {
    private final String tableNumber;
    private final int capacity;
    private boolean isOccupied;

    public Table(String tableNumber, int capacity) {
        this.tableNumber = Objects.requireNonNull(tableNumber, "Table number cannot be null");
        if (capacity <= 0) {
            throw new IllegalArgumentException("Table capacity must be positive");
        }
        this.capacity = capacity;
        this.isOccupied = false;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized boolean isOccupied() {
        return isOccupied;
    }

    public synchronized void markOccupied() {
        this.isOccupied = true;
    }

    public synchronized void markAvailable() {
        this.isOccupied = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Table table)) return false;
        return Objects.equals(tableNumber, table.tableNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableNumber);
    }
}

