package com.restaurant.strategy.table;

import com.restaurant.model.Customer;
import com.restaurant.model.Table;

import java.util.List;

public class FirstAvailableAllocationStrategy implements TableAllocationStrategy {
    @Override
    public Table allocateTable(List<Table> availableTables, int guestCount, Customer customer) {
        if (availableTables == null) return null;
        return availableTables.stream()
                .filter(t -> !t.isOccupied() && t.getCapacity() >= guestCount)
                .findFirst()
                .orElse(null);
    }
}
