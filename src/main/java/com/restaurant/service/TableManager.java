package com.restaurant.service;

import com.restaurant.model.Customer;
import com.restaurant.model.Table;
import com.restaurant.strategy.table.FirstAvailableAllocationStrategy;
import com.restaurant.strategy.table.TableAllocationStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TableManager {
    private final List<Table> tables;
    private TableAllocationStrategy allocationStrategy;

    public TableManager() {
        this(new ArrayList<>(), new FirstAvailableAllocationStrategy());
    }

    public TableManager(List<Table> tables, TableAllocationStrategy strategy) {
        this.tables = new ArrayList<>(Objects.requireNonNull(tables, "Tables cannot be null"));
        this.allocationStrategy = Objects.requireNonNull(strategy, "Allocation strategy cannot be null");
    }

    public synchronized void addTable(Table table) {
        Objects.requireNonNull(table, "Table cannot be null");
        if (!tables.contains(table)) {
            tables.add(table);
        }
    }

    public synchronized List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public synchronized List<Table> getAvailableTables() {
        return tables.stream()
                .filter(t -> !t.isOccupied())
                .collect(Collectors.toList());
    }

    public synchronized Table allocateTable(int guestCount, Customer customer) {
        List<Table> available = getAvailableTables();
        return allocationStrategy.allocateTable(available, guestCount, customer);
    }

    public synchronized void setAllocationStrategy(TableAllocationStrategy strategy) {
        this.allocationStrategy = Objects.requireNonNull(strategy, "Allocation strategy cannot be null");
    }

    public synchronized TableAllocationStrategy getAllocationStrategy() {
        return allocationStrategy;
    }
}
