package com.restaurant.model;

import com.restaurant.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation extends BaseEntity {
    private final Customer customer;
    private final Table table;
    private final LocalDateTime reservationDateTime;
    private final int guestCount;
    private ReservationStatus status;
    private String specialRequests;

    public Reservation(String reservationId, Customer customer, Table table, LocalDateTime reservationDateTime, int guestCount) {
        this(reservationId, customer, table, reservationDateTime, guestCount, "");
    }

    public Reservation(String reservationId, Customer customer, Table table, LocalDateTime reservationDateTime, int guestCount, String specialRequests) {
        super(reservationId);
        this.customer = Objects.requireNonNull(customer, "Customer cannot be null");
        this.table = Objects.requireNonNull(table, "Table cannot be null");
        this.reservationDateTime = Objects.requireNonNull(reservationDateTime, "Reservation date time cannot be null");
        if (guestCount <= 0) {
            throw new IllegalArgumentException("Guest count must be positive");
        }
        this.guestCount = guestCount;
        this.status = ReservationStatus.PENDING;
        this.specialRequests = specialRequests != null ? specialRequests : "";
    }

    public Customer getCustomer() {
        return customer;
    }

    public Table getTable() {
        return table;
    }

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void confirm() {
        if (status == ReservationStatus.PENDING) {
            this.status = ReservationStatus.CONFIRMED;
            markUpdated();
        }
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        markUpdated();
    }

    public void markCompleted() {
        this.status = ReservationStatus.COMPLETED;
        markUpdated();
    }

    public void markNoShow() {
        this.status = ReservationStatus.NO_SHOW;
        markUpdated();
    }
}
