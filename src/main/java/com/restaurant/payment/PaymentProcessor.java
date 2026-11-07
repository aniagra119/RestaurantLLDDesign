package com.restaurant.payment;

import com.restaurant.billing.Payment;

public interface PaymentProcessor {
    PaymentResult processPayment(Payment payment);
    PaymentResult refund(Payment payment, double amount);
}
