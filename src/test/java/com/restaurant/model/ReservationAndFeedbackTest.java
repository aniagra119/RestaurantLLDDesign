package com.restaurant.model;

import com.restaurant.enums.ReservationStatus;
import com.restaurant.enums.WaiterLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reservation & Customer Feedback Tests")
class ReservationAndFeedbackTest {

    @Test
    @DisplayName("Should manage Reservation lifecycle states correctly")
    void testReservationLifecycle() {
        Customer customer = new Customer("C-10", "Varun", "9812345678", "varun@test.com");
        Table table = new Table("T-4", 4);
        LocalDateTime slot = LocalDateTime.now().plusDays(1);

        Reservation reservation = new Reservation("RES-001", customer, table, slot, 4, "Window seat preferred");
        assertEquals("RES-001", reservation.getId());
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertEquals("Window seat preferred", reservation.getSpecialRequests());

        // Confirm
        reservation.confirm();
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());

        // Cancel
        reservation.cancel();
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());

        // Complete
        reservation.markCompleted();
        assertEquals(ReservationStatus.COMPLETED, reservation.getStatus());
    }

    @Test
    @DisplayName("Should capture CustomerFeedback with clamped ratings between 1 and 5")
    void testCustomerFeedbackRatingClamping() {
        Customer customer = new Customer("C-20", "Pooja", "9876543210", "pooja@test.com");
        Table table = new Table("T-2", 2);
        Waiter waiter = new Waiter("W-1", "Aman", "aman@test.com", "9800000000", WaiterLevel.JUNIOR);
        CustomerVisit visit = new CustomerVisit("VISIT-100", customer, table, waiter);

        CustomerFeedback feedbackHigh = new CustomerFeedback("FB-1", visit, 10, "Exceptional dinner!");
        assertEquals(5, feedbackHigh.getRating(), "Ratings above 5 must clamp to 5");
        assertEquals("Exceptional dinner!", feedbackHigh.getComments());

        CustomerFeedback feedbackLow = new CustomerFeedback("FB-2", visit, -3, "Disappointing wait time");
        assertEquals(1, feedbackLow.getRating(), "Ratings below 1 must clamp to 1");

        CustomerFeedback feedbackNormal = new CustomerFeedback("FB-3", visit, 4, "Great food and prompt service");
        assertEquals(4, feedbackNormal.getRating());
    }
}
