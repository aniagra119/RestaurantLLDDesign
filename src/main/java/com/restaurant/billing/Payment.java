package com.restaurant.billing;

import com.restaurant.enums.PaymentMethod;

import java.time.Instant;
import java.util.Objects;

public class Payment {
    private final String paymentId;
    private final double amount;
    private final PaymentMethod method;
    private final String transactionReference;
    private final long timestamp;

    public Payment(String paymentId, double amount, PaymentMethod method, String transactionReference) {
        this.paymentId = Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        this.amount = amount;
        this.method = Objects.requireNonNull(method, "Payment method cannot be null");
        this.transactionReference = transactionReference;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

