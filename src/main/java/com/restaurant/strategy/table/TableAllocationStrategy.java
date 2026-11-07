package com.restaurant.strategy.table;

import com.restaurant.model.Customer;
import com.restaurant.model.Table;

import java.util.List;

public interface TableAllocationStrategy {
    Table allocateTable(List<Table> availableTables, int guestCount, Customer customer);
}
