package com.restaurant.payment;

import com.restaurant.enums.PaymentMethod;
import com.restaurant.payment.processor.CardPaymentProcessor;
import com.restaurant.payment.processor.CashPaymentProcessor;
import com.restaurant.payment.processor.UPIPaymentProcessor;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class PaymentProcessorFactory {
    private final Map<PaymentMethod, PaymentProcessor> registry = new EnumMap<>(PaymentMethod.class);

    public PaymentProcessorFactory() {
        registerDefaultProcessors();
    }

    private void registerDefaultProcessors() {
        registry.put(PaymentMethod.CASH, new CashPaymentProcessor());
        registry.put(PaymentMethod.CREDIT_CARD, new CardPaymentProcessor());
        registry.put(PaymentMethod.DEBIT_CARD, new CardPaymentProcessor());
        registry.put(PaymentMethod.UPI, new UPIPaymentProcessor());
    }

    public void registerProcessor(PaymentMethod method, PaymentProcessor processor) {
        registry.put(Objects.requireNonNull(method), Objects.requireNonNull(processor));
    }

    public PaymentProcessor getProcessor(PaymentMethod method) {
        PaymentProcessor processor = registry.get(method);
        if (processor == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
        return processor;
    }
}
