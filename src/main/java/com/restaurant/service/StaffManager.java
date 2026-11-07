package com.restaurant.service;

import com.restaurant.enums.KitchenStation;
import com.restaurant.model.Chef;
import com.restaurant.model.Table;
import com.restaurant.model.Waiter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class StaffManager {
    private final List<Waiter> waiters = new ArrayList<>();
    private final List<Chef> chefs = new ArrayList<>();

    public synchronized void addWaiter(Waiter waiter) {
        Objects.requireNonNull(waiter, "Waiter cannot be null");
        if (!waiters.contains(waiter)) {
            waiters.add(waiter);
        }
    }

    public synchronized void addChef(Chef chef) {
        Objects.requireNonNull(chef, "Chef cannot be null");
        if (!chefs.contains(chef)) {
            chefs.add(chef);
        }
    }

    public synchronized List<Waiter> getWaiters() {
        return Collections.unmodifiableList(waiters);
    }

    public synchronized List<Chef> getChefs() {
        return Collections.unmodifiableList(chefs);
    }

    public synchronized Waiter assignWaiter(Table table) {
        Objects.requireNonNull(table, "Table cannot be null");
        Waiter bestWaiter = waiters.stream()
                .min(Comparator.comparingInt(w -> w.getAssignedTables().size()))
                .orElseThrow(() -> new IllegalStateException("No waiters registered in the restaurant"));
        bestWaiter.assignTable(table);
        return bestWaiter;
    }

    public synchronized Optional<Chef> findAvailableChef(KitchenStation station) {
        return chefs.stream()
                .filter(Chef::canAcceptOrder)
                .min(Comparator.comparingInt((Chef c) -> c.getPrimaryStation() == station ? 0 : 1)
                        .thenComparingInt(c -> c.getCurrentOrders().size()));
    }
}
