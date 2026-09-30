package com.restaurant.model;

import java.util.Objects;

public abstract class Employee {
    protected final String id;
    protected final String name;
    protected final String email;
    protected final String phoneNumber;

    public Employee(String id, String name, String email, String phoneNumber) {
        this.id = Objects.requireNonNull(id, "Employee ID cannot be null");
        this.name = Objects.requireNonNull(name, "Employee name cannot be null");
        this.email = email;
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "Phone number cannot be null");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Employee employee))
            return false;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
