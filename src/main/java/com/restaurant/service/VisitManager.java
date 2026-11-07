package com.restaurant.service;

import com.restaurant.billing.Bill;
import com.restaurant.billing.Payment;
import com.restaurant.billing.strategy.BillingStrategy;
import com.restaurant.event.OrderEventPublisher;
import com.restaurant.event.OrderPlacedEvent;
import com.restaurant.exception.TableAlreadyOccupiedException;
import com.restaurant.model.Customer;
import com.restaurant.model.CustomerVisit;
import com.restaurant.model.Order;
import com.restaurant.model.Table;
import com.restaurant.model.Waiter;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VisitManager {
    private final Map<String, CustomerVisit> activeVisits = new ConcurrentHashMap<>();
    private final Map<String, String> tableToVisitMap = new ConcurrentHashMap<>();
    private final OrderEventPublisher eventPublisher;

    public VisitManager(OrderEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher cannot be null");
    }

    public synchronized CustomerVisit startVisit(Customer customer, Table table, Waiter waiter) {
        Objects.requireNonNull(customer, "Customer cannot be null");
        Objects.requireNonNull(table, "Table cannot be null");
        Objects.requireNonNull(waiter, "Waiter cannot be null");

        if (table.isOccupied() || tableToVisitMap.containsKey(table.getTableNumber())) {
            throw new TableAlreadyOccupiedException("Table " + table.getTableNumber() + " is already occupied");
        }

        String visitId = "VISIT-" + UUID.randomUUID().toString().substring(0, 8);
        CustomerVisit visit = new CustomerVisit(visitId, customer, table, waiter);

        activeVisits.put(visitId, visit);
        tableToVisitMap.put(table.getTableNumber(), visitId);
        return visit;
    }

    public CustomerVisit getVisit(String visitId) {
        return activeVisits.get(visitId);
    }

    public Map<String, CustomerVisit> getActiveVisits() {
        return Collections.unmodifiableMap(activeVisits);
    }

    public synchronized void placeOrder(String visitId, Order order) {
        CustomerVisit visit = activeVisits.get(visitId);
        if (visit == null) {
            throw new IllegalArgumentException("No active visit found for ID: " + visitId);
        }
        visit.placeOrder(order);
        eventPublisher.publishOrderPlaced(new OrderPlacedEvent(
                UUID.randomUUID().toString(), visit, order));
    }

    public synchronized Bill generateBill(String visitId, BillingStrategy strategy) {
        CustomerVisit visit = activeVisits.get(visitId);
        if (visit == null) {
            throw new IllegalArgumentException("No active visit found for ID: " + visitId);
        }
        return visit.generateBill(strategy);
    }

    public synchronized void settlePayment(String visitId, Payment payment) {
        CustomerVisit visit = activeVisits.get(visitId);
        if (visit == null) {
            throw new IllegalArgumentException("No active visit found for ID: " + visitId);
        }
        visit.makePayment(payment);
    }

    public synchronized void endVisit(String visitId) {
        CustomerVisit visit = activeVisits.get(visitId);
        if (visit == null) {
            throw new IllegalArgumentException("No active visit found for ID: " + visitId);
        }
        visit.completeVisit();
        tableToVisitMap.remove(visit.getTable().getTableNumber());
        activeVisits.remove(visitId);
    }
}

