package com.restaurant.service;

import com.restaurant.enums.SessionStatus;
import com.restaurant.model.Customer;
import com.restaurant.model.DiningSession;
import com.restaurant.model.Waiter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DiningSessionManager {
    private final Map<String, DiningSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, String> tableToSessionMap = new ConcurrentHashMap<>();

    public synchronized DiningSession openSession(String sessionId, String tableNumber, Customer customer, Waiter waiter) {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        Objects.requireNonNull(tableNumber, "Table number cannot be null");
        Objects.requireNonNull(customer, "Customer cannot be null");
        Objects.requireNonNull(waiter, "Waiter cannot be null");

        if (tableToSessionMap.containsKey(tableNumber)) {
            throw new IllegalStateException("Table " + tableNumber + " is already occupied by session " + tableToSessionMap.get(tableNumber));
        }

        DiningSession session = new DiningSession(sessionId, tableNumber, customer, waiter);
        activeSessions.put(sessionId, session);
        tableToSessionMap.put(tableNumber, sessionId);
        waiter.assignTable(tableNumber);
        return session;
    }

    public Optional<DiningSession> getSessionById(String sessionId) {
        return Optional.ofNullable(activeSessions.get(sessionId));
    }

    public Optional<DiningSession> getSessionByTable(String tableNumber) {
        String sessionId = tableToSessionMap.get(tableNumber);
        if (sessionId != null) {
            return getSessionById(sessionId);
        }
        return Optional.empty();
    }

    public synchronized void closeSession(String sessionId) {
        DiningSession session = activeSessions.get(sessionId);
        if (session != null) {
            if (session.getStatus() != SessionStatus.SETTLED) {
                throw new IllegalStateException("Cannot close session " + sessionId + " because it is not SETTLED");
            }
            tableToSessionMap.remove(session.getTableNumber());
            session.getAssignedWaiter().unassignTable(session.getTableNumber());
            activeSessions.remove(sessionId);
        }
    }
}

