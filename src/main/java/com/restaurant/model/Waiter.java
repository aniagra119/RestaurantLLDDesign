package com.restaurant.model;

import com.restaurant.enums.WaiterLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Waiter extends Employee {
    private final WaiterLevel level;
    private final List<Table> assignedTables;

    public Waiter(String id, String name, String email, String phoneNumber, WaiterLevel level) {
        super(id, name, email, phoneNumber);
        this.level = Objects.requireNonNull(level, "Waiter level cannot be null");
        this.assignedTables = new ArrayList<>();
    }

    public WaiterLevel getLevel() {
        return level;
    }

    public List<Table> getAssignedTables() {
        return Collections.unmodifiableList(assignedTables);
    }

    public synchronized void assignTable(Table table) {
        Objects.requireNonNull(table, "Table cannot be null");
        if (!assignedTables.contains(table)) {
            assignedTables.add(table);
        }
    }

    public synchronized void releaseTable(Table table) {
        assignedTables.remove(table);
    }
}
