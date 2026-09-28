package com.restaurant.model;

import com.restaurant.billing.Bill;
import com.restaurant.billing.Payment;
import com.restaurant.billing.strategy.BillingStrategy;
import com.restaurant.enums.VisitStatus;
import com.restaurant.exception.InvalidVisitStateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CustomerVisit {
    private final String visitId;
    private final Table table;
    private final Customer customer;
    private final Waiter waiter;
    private final List<Order> orders;
    private Bill bill;
    private VisitStatus status;

    public CustomerVisit(String visitId, Customer customer, Table table, Waiter waiter) {
        this.visitId = Objects.requireNonNull(visitId, "Visit ID cannot be null");
        this.customer = Objects.requireNonNull(customer, "Customer cannot be null");
        this.table = Objects.requireNonNull(table, "Table cannot be null");
        this.waiter = Objects.requireNonNull(waiter, "Waiter cannot be null");
        this.orders = new ArrayList<>();
        this.status = VisitStatus.ACTIVE;

        table.markOccupied();
        waiter.assignTable(table);
    }

    public String getVisitId() {
        return visitId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Table getTable() {
        return table;
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public synchronized List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public synchronized Bill getBill() {
        return bill;
    }

    public synchronized VisitStatus getStatus() {
        return status;
    }

    public synchronized void placeOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        if (status != VisitStatus.ACTIVE) {
            throw new InvalidVisitStateException("Cannot place orders. Visit is currently: " + status);
        }
        orders.add(order);
    }

    public synchronized Bill generateBill(BillingStrategy billingStrategy) {
        Objects.requireNonNull(billingStrategy, "Billing strategy cannot be null");
        if (status == VisitStatus.COMPLETED) {
            throw new InvalidVisitStateException("Visit is already completed");
        }
        this.bill = billingStrategy.calculateBill("BILL-" + visitId, orders);
        this.status = VisitStatus.BILLED;
        return bill;
    }

    public synchronized void makePayment(Payment payment) {
        Objects.requireNonNull(payment, "Payment cannot be null");
        if (bill == null) {
            throw new InvalidVisitStateException("Cannot process payment before bill is generated");
        }
        bill.addPayment(payment);
    }

    public synchronized void completeVisit() {
        if (bill == null || !bill.isFullyPaid()) {
            throw new InvalidVisitStateException("Cannot complete visit until bill is fully settled");
        }
        this.status = VisitStatus.COMPLETED;
        table.markAvailable();
        waiter.releaseTable(table);
    }
}
