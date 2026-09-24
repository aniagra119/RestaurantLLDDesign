package com.restaurant.model;

import com.restaurant.enums.BillStatus;
import com.restaurant.enums.SessionStatus;
import com.restaurant.strategy.billing.BillingStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DiningSession {
    private final String sessionId;
    private final String tableNumber;
    private final Customer primaryCustomer;
    private final List<Customer> partyMembers;
    private final Waiter assignedWaiter;
    private final LocalDateTime startedAt;
    private LocalDateTime closedAt;
    private SessionStatus status;
    private final List<Order> orders;
    private Bill bill;

    public DiningSession(String sessionId, String tableNumber, Customer primaryCustomer, Waiter assignedWaiter) {
        this.sessionId = Objects.requireNonNull(sessionId, "Session ID cannot be null");
        this.tableNumber = Objects.requireNonNull(tableNumber, "Table number cannot be null");
        this.primaryCustomer = Objects.requireNonNull(primaryCustomer, "Primary customer cannot be null");
        this.assignedWaiter = Objects.requireNonNull(assignedWaiter, "Assigned waiter cannot be null");
        this.startedAt = LocalDateTime.now();
        this.status = SessionStatus.ACTIVE;
        this.partyMembers = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public Customer getPrimaryCustomer() {
        return primaryCustomer;
    }

    public Waiter getAssignedWaiter() {
        return assignedWaiter;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public synchronized SessionStatus getStatus() {
        return status;
    }

    public synchronized void addPartyMember(Customer customer) {
        Objects.requireNonNull(customer, "Customer cannot be null");
        this.partyMembers.add(customer);
    }

    public synchronized List<Customer> getPartyMembers() {
        return Collections.unmodifiableList(partyMembers);
    }

    public synchronized void addOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        if (this.status != SessionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot place order. Session is in status: " + status);
        }
        this.orders.add(order);
    }

    public synchronized List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public synchronized Bill generateBill(BillingStrategy billingStrategy) {
        Objects.requireNonNull(billingStrategy, "BillingStrategy cannot be null");
        if (this.status == SessionStatus.SETTLED) {
            throw new IllegalStateException("Session is already settled");
        }
        this.bill = billingStrategy.computeBill(this.sessionId, this.orders);
        this.status = SessionStatus.BILLED;
        return this.bill;
    }

    public synchronized Bill getBill() {
        return bill;
    }

    public synchronized void applyPayment(Payment payment) {
        if (this.bill == null) {
            throw new IllegalStateException("Cannot apply payment before bill is generated");
        }
        this.bill.addPayment(payment);
        if (this.bill.getBillStatus() == BillStatus.PAID) {
            this.status = SessionStatus.SETTLED;
            this.closedAt = LocalDateTime.now();
        }
    }
}

