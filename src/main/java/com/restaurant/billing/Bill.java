package com.restaurant.billing;

import com.restaurant.enums.BillStatus;
import com.restaurant.exception.InvalidPaymentException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Bill {
    private final String billId;
    private final double itemSubtotal;
    private final double taxAmount;
    private final double serviceCharge;
    private final double discountAmount;
    private final double totalPayable;
    private BillStatus status;
    private final List<Payment> payments;

    public Bill(String billId, double itemSubtotal, double taxAmount, double serviceCharge, double discountAmount, double totalPayable) {
        this.billId = Objects.requireNonNull(billId, "Bill ID cannot be null");
        this.itemSubtotal = itemSubtotal;
        this.taxAmount = taxAmount;
        this.serviceCharge = serviceCharge;
        this.discountAmount = discountAmount;
        this.totalPayable = Math.max(0.0, totalPayable);
        this.status = BillStatus.UNPAID;
        this.payments = new ArrayList<>();
    }

    public String getBillId() {
        return billId;
    }

    public double getItemSubtotal() {
        return itemSubtotal;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getTotalPayable() {
        return totalPayable;
    }

    public synchronized BillStatus getStatus() {
        return status;
    }

    public synchronized List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    public synchronized void addPayment(Payment payment) {
        Objects.requireNonNull(payment, "Payment cannot be null");
        if (status == BillStatus.PAID) {
            throw new IllegalStateException("Bill is already fully paid");
        }
        double remaining = getRemainingAmount();
        if (payment.getAmount() > remaining + 0.001) {
            throw new InvalidPaymentException(String.format(
                    "Payment amount (%.2f) exceeds remaining balance (%.2f)", payment.getAmount(), remaining));
        }

        payments.add(payment);

        if (getRemainingAmount() <= 0.001) {
            this.status = BillStatus.PAID;
        } else {
            this.status = BillStatus.PARTIALLY_PAID;
        }
    }

    public synchronized double getRemainingAmount() {
        double paidSoFar = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
        return Math.max(0.0, totalPayable - paidSoFar);
    }

    public synchronized boolean isFullyPaid() {
        return status == BillStatus.PAID || getRemainingAmount() <= 0.001;
    }
}
