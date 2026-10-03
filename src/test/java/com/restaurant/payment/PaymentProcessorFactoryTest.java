package com.restaurant.payment;

import com.restaurant.billing.Payment;
import com.restaurant.enums.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Payment Gateway Processors & Factory Registry Tests")
class PaymentProcessorFactoryTest {

    @Test
    @DisplayName("Should resolve default payment processors for all payment methods")
    void testDefaultProcessorResolution() {
        PaymentProcessorFactory factory = new PaymentProcessorFactory();

        PaymentProcessor cashProc = factory.getProcessor(PaymentMethod.CASH);
        assertNotNull(cashProc);

        PaymentProcessor cardProc = factory.getProcessor(PaymentMethod.CREDIT_CARD);
        assertNotNull(cardProc);

        PaymentProcessor debitProc = factory.getProcessor(PaymentMethod.DEBIT_CARD);
        assertNotNull(debitProc);

        PaymentProcessor upiProc = factory.getProcessor(PaymentMethod.UPI);
        assertNotNull(upiProc);
    }

    @Test
    @DisplayName("Should process cash, card, and UPI payments successfully")
    void testPaymentProcessingAndRefunds() {
        PaymentProcessorFactory factory = new PaymentProcessorFactory();

        Payment payment = new Payment("P-TEST", 500.0, PaymentMethod.UPI, "UPI-REF");
        PaymentProcessor upiProcessor = factory.getProcessor(PaymentMethod.UPI);

        PaymentResult result = upiProcessor.processPayment(payment);
        assertTrue(result.isSuccess());
        assertNotNull(result.getTransactionId());
        assertNull(result.getErrorMessage());

        PaymentResult refundResult = upiProcessor.refund(payment, 500.0);
        assertTrue(refundResult.isSuccess());
        assertTrue(refundResult.getTransactionId().contains("REFUND"));
    }

    @Test
    @DisplayName("Should allow registering custom processors via Registry Pattern")
    void testCustomProcessorRegistration() {
        PaymentProcessorFactory factory = new PaymentProcessorFactory();

        PaymentProcessor customCryptoProcessor = new PaymentProcessor() {
            @Override
            public PaymentResult processPayment(Payment payment) {
                return PaymentResult.success("CRYPTO-HASH-0x123");
            }

            @Override
            public PaymentResult refund(Payment payment, double amount) {
                return PaymentResult.success("CRYPTO-REFUND-0x123");
            }
        };

        factory.registerProcessor(PaymentMethod.UPI, customCryptoProcessor);
        PaymentResult res = factory.getProcessor(PaymentMethod.UPI)
                .processPayment(new Payment("P-CRYPTO", 1000.0, PaymentMethod.UPI, "BLOCKCHAIN"));

        assertTrue(res.isSuccess());
        assertEquals("CRYPTO-HASH-0x123", res.getTransactionId());
    }
}
