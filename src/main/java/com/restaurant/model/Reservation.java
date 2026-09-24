package com.restaurant.model;

import com.restaurant.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation {
    private final String id;
    private final Customer customer;
    private final String tableNumber;
    private final int partySize;
    private final LocalDateTime reservedSlot;
    private ReservationStatus status;

    public Reservation(String id, Customer customer, String tableNumber, int partySize, LocalDateTime reservedSlot) {
        this.id = Objects.requireNonNull(id, "Reservation ID cannot be null");
        this.customer = Objects.requireNonNull(customer, "Customer cannot be null");
        this.tableNumber = Objects.requireNonNull(tableNumber, "Table number cannot be null");
        if (partySize <= 0) {
            throw new IllegalArgumentException("Party size must be positive");
        }
        this.partySize = partySize;
        this.reservedSlot = Objects.requireNonNull(reservedSlot, "Reserved slot cannot be null");
        this.status = ReservationStatus.CONFIRMED;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public int getPartySize() {
        return partySize;
    }

    public LocalDateTime getReservedSlot() {
        return reservedSlot;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void cancel() {
        if (this.status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel reservation in state " + status);
        }
        this.status = ReservationStatus.CANCELLED;
    }

    public DiningSession checkIn(String sessionId, Waiter assignedWaiter) {
        if (this.status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot check in reservation in state " + status);
        }
        this.status = ReservationStatus.SEATED;
        return new DiningSession(sessionId, this.tableNumber, this.customer, assignedWaiter);
    }
}

