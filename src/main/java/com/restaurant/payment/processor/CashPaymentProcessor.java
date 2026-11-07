package com.restaurant.payment.processor;

import com.restaurant.billing.Payment;
import com.restaurant.payment.PaymentProcessor;
import com.restaurant.payment.PaymentResult;

public class CashPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentResult processPayment(Payment payment) {
        if (payment == null || payment.getAmount() <= 0) {
            return PaymentResult.failure("Invalid payment amount");
        }
        return PaymentResult.success("CASH-TXN-" + System.currentTimeMillis());
    }

    @Override
    public PaymentResult refund(Payment payment, double amount) {
        return PaymentResult.success("CASH-REFUND-" + payment.getPaymentId());
    }
}
