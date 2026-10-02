package com.restaurant.payment.processor;

import com.restaurant.billing.Payment;
import com.restaurant.payment.PaymentProcessor;
import com.restaurant.payment.PaymentResult;

public class CardPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentResult processPayment(Payment payment) {
        if (payment == null || payment.getAmount() <= 0) {
            return PaymentResult.failure("Invalid card payment amount");
        }
        return PaymentResult.success("CARD-TXN-" + System.currentTimeMillis());
    }

    @Override
    public PaymentResult refund(Payment payment, double amount) {
        return PaymentResult.success("CARD-REFUND-" + payment.getPaymentId());
    }
}
