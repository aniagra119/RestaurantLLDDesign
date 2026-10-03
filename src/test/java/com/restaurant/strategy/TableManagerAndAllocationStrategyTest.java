package com.restaurant.strategy;

import com.restaurant.model.Customer;
import com.restaurant.model.Table;
import com.restaurant.service.TableManager;
import com.restaurant.strategy.table.FirstAvailableAllocationStrategy;
import com.restaurant.strategy.table.OptimalFitAllocationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Table Allocation Strategy & TableManager Tests")
class TableManagerAndAllocationStrategyTest {

    @Test
    @DisplayName("Should allocate first available table with FirstAvailableAllocationStrategy")
    void testFirstAvailableStrategy() {
        Table tableSmall = new Table("T-2", 2);
        Table tableLarge = new Table("T-6", 6);
        Table tableMedium = new Table("T-4", 4);

        TableManager manager = new TableManager(List.of(tableLarge, tableMedium, tableSmall), new FirstAvailableAllocationStrategy());
        Customer customer = new Customer("C-1", "Aditi", "9811122233", "aditi@test.com");

        // Party of 2 should get the first table in the list with capacity >= 2 (which is tableLarge T-6)
        Table allocated = manager.allocateTable(2, customer);
        assertNotNull(allocated);
        assertEquals("T-6", allocated.getTableNumber());
    }

    @Test
    @DisplayName("Should allocate optimal smallest fitting table with OptimalFitAllocationStrategy")
    void testOptimalFitStrategy() {
        Table tableSmall = new Table("T-2", 2);
        Table tableLarge = new Table("T-8", 8);
        Table tableMedium = new Table("T-4", 4);

        TableManager manager = new TableManager(List.of(tableLarge, tableMedium, tableSmall), new OptimalFitAllocationStrategy());
        Customer customer = new Customer("C-2", "Vikram", "9811100000", "v@test.com");

        // Party of 3: tableSmall (2) is too small; tableMedium (4) fits with 1 empty cover; tableLarge (8) wastes 5 covers.
        // Optimal fit must select T-4
        Table allocated = manager.allocateTable(3, customer);
        assertNotNull(allocated);
        assertEquals("T-4", allocated.getTableNumber());

        // Party of 2: should select T-2 exactly
        Table allocatedPartyOf2 = manager.allocateTable(2, customer);
        assertNotNull(allocatedPartyOf2);
        assertEquals("T-2", allocatedPartyOf2.getTableNumber());

        // Party of 10: exceeds largest table capacity -> returns null
        Table overCapacity = manager.allocateTable(10, customer);
        assertNull(overCapacity);
    }

    @Test
    @DisplayName("Should dynamically swap allocation strategies at runtime")
    void testDynamicStrategySwapping() {
        Table t6 = new Table("T-6", 6);
        Table t2 = new Table("T-2", 2);

        TableManager manager = new TableManager(List.of(t6, t2), new FirstAvailableAllocationStrategy());
        Customer customer = new Customer("C-3", "Rohan", "9800000000", "r@test.com");

        // First available strategy returns T-6
        assertEquals("T-6", manager.allocateTable(2, customer).getTableNumber());

        // Swap to Optimal Fit strategy -> now returns T-2
        manager.setAllocationStrategy(new OptimalFitAllocationStrategy());
        assertEquals("T-2", manager.allocateTable(2, customer).getTableNumber());
    }
}
