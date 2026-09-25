package com.restaurant.model;

import com.restaurant.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Payment {
    private final String paymentId;
    private final BigDecimal amount;
    private final PaymentMethod method;
    private final String transactionReference;
    private final LocalDateTime paidAt;

    public Payment(String paymentId, BigDecimal amount, PaymentMethod method, String transactionReference) {
        this.paymentId = Objects.requireNonNull(paymentId, "Payment ID cannot be null");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        this.amount = amount;
        this.method = Objects.requireNonNull(method, "PaymentMethod cannot be null");
        this.transactionReference = transactionReference != null ? transactionReference : "";
        this.paidAt = LocalDateTime.now();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}

