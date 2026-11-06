package com.restaurant.model;

import com.restaurant.enums.BillStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Bill {
    private final String billId;
    private final String sessionId;
    private final LocalDateTime generatedAt;
    private final BigDecimal itemSubtotal;
    private final BigDecimal taxAmount;
    private final BigDecimal serviceCharge;
    private final BigDecimal discountAmount;
    private final BigDecimal totalPayable;
    private BillStatus billStatus;
    private final List<Payment> payments;
    private LocalDateTime settledAt;

    public Bill(String billId, String sessionId, BigDecimal itemSubtotal, BigDecimal taxAmount,
                BigDecimal serviceCharge, BigDecimal discountAmount) {
        this.billId = Objects.requireNonNull(billId, "Bill ID cannot be null");
        this.sessionId = Objects.requireNonNull(sessionId, "Session ID cannot be null");
        this.generatedAt = LocalDateTime.now();
        this.itemSubtotal = itemSubtotal.setScale(2, RoundingMode.HALF_UP);
        this.taxAmount = taxAmount.setScale(2, RoundingMode.HALF_UP);
        this.serviceCharge = serviceCharge.setScale(2, RoundingMode.HALF_UP);
        this.discountAmount = discountAmount.setScale(2, RoundingMode.HALF_UP);
        this.totalPayable = this.itemSubtotal
                .add(this.taxAmount)
                .add(this.serviceCharge)
                .subtract(this.discountAmount)
                .setScale(2, RoundingMode.HALF_UP);
        this.billStatus = BillStatus.UNPAID;
        this.payments = new ArrayList<>();
    }

    public String getBillId() {
        return billId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public BigDecimal getItemSubtotal() {
        return itemSubtotal;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTotalPayable() {
        return totalPayable;
    }

    public synchronized BillStatus getBillStatus() {
        return billStatus;
    }

    public synchronized List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    public synchronized BigDecimal getRemainingAmount() {
        BigDecimal totalPaid = BigDecimal.ZERO;
        for (Payment p : payments) {
            totalPaid = totalPaid.add(p.getAmount());
        }
        BigDecimal remaining = totalPayable.subtract(totalPaid).setScale(2, RoundingMode.HALF_UP);
        return remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : remaining;
    }

    public synchronized boolean isFullyPaid() {
        return getRemainingAmount().compareTo(BigDecimal.ZERO) == 0 && !payments.isEmpty();
    }

    public synchronized void addPayment(Payment payment) {
        Objects.requireNonNull(payment, "Payment cannot be null");
        if (this.billStatus == BillStatus.PAID) {
            throw new IllegalStateException("Bill is already fully paid");
        }
        BigDecimal remaining = getRemainingAmount();
        if (payment.getAmount().compareTo(remaining) > 0) {
            throw new IllegalArgumentException("Payment amount (" + payment.getAmount() +
                    ") exceeds remaining balance (" + remaining + ")");
        }
        this.payments.add(payment);
        if (isFullyPaid()) {
            this.billStatus = BillStatus.PAID;
            this.settledAt = LocalDateTime.now();
        } else {
            this.billStatus = BillStatus.PARTIALLY_PAID;
        }
    }

    public LocalDateTime getSettledAt() {
        return settledAt;
    }
}

