package com.restaurant;

import com.restaurant.enums.Department;
import com.restaurant.enums.Permission;
import com.restaurant.enums.ReservationStatus;
import com.restaurant.enums.SessionStatus;
import com.restaurant.model.Customer;
import com.restaurant.model.DiningSession;
import com.restaurant.model.Reservation;
import com.restaurant.model.Role;
import com.restaurant.model.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reservation Lifecycle Test Suite")
class ReservationLifecycleTest {

    @Test
    @DisplayName("Should confirm reservation and check-in to spawn active DiningSession")
    void testReservationCheckIn() {
        Customer customer = new Customer("C-100", "Ananya Roy", "9123456789", "ananya@example.com");
        LocalDateTime bookingTime = LocalDateTime.now().plusHours(2);
        Reservation reservation = new Reservation("RES-001", customer, "Table-12", 4, bookingTime);

        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
        assertEquals("Table-12", reservation.getTableNumber());

        Role waiterRole = new Role("R-W", "Server", Department.SERVICE, Set.of(Permission.TAKE_ORDER));
        Waiter waiter = new Waiter("W-10", "Sunil", "sunil@restaurant.com", "9876543210", waiterRole);

        DiningSession session = reservation.checkIn("SESS-100", waiter);

        assertEquals(ReservationStatus.SEATED, reservation.getStatus());
        assertEquals(SessionStatus.ACTIVE, session.getStatus());
        assertEquals("Table-12", session.getTableNumber());
        assertEquals(customer, session.getPrimaryCustomer());
    }

    @Test
    @DisplayName("Should cancel reservation successfully")
    void testReservationCancellation() {
        Customer customer = new Customer("C-101", "Karan Johar", "9123456780", "karan@example.com");
        Reservation reservation = new Reservation("RES-002", customer, "Table-3", 2, LocalDateTime.now().plusDays(1));

        reservation.cancel();
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());

        Role waiterRole = new Role("R-W", "Server", Department.SERVICE, Set.of(Permission.TAKE_ORDER));
        Waiter waiter = new Waiter("W-10", "Sunil", "sunil@restaurant.com", "9876543210", waiterRole);

        assertThrows(IllegalStateException.class, () -> reservation.checkIn("SESS-FAIL", waiter));
    }
}

