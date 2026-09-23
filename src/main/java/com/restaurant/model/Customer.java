package com.restaurant.model;

import java.util.Objects;

public class Customer {
    private final String id;
    private final String name;
    private final String phoneNumber;
    private final String email;

    public Customer(String id, String name, String phoneNumber, String email) {
        this.id = Objects.requireNonNull(id, "Customer ID cannot be null");
        this.name = Objects.requireNonNull(name, "Customer name cannot be null");
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}

