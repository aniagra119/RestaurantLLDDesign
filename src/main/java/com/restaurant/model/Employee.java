package com.restaurant.model;

import com.restaurant.enums.Department;
import com.restaurant.enums.Permission;

import java.util.Objects;

public abstract class Employee {
    protected final String id;
    protected final String name;
    protected final String email;
    protected final String phoneNumber;
    protected final Department department;
    protected Role role;

    protected Employee(String id, String name, String email, String phoneNumber, Department department, Role role) {
        this.id = Objects.requireNonNull(id, "Employee ID must not be null");
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = Objects.requireNonNull(department, "Department must not be null");
        this.role = Objects.requireNonNull(role, "Role must not be null");
        if (role.getDepartment() != department) {
            throw new IllegalArgumentException("Role department does not match employee department");
        }
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

    public Department getDepartment() {
        return department;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        if (role.getDepartment() != this.department) {
            throw new IllegalArgumentException("Role department does not match employee department");
        }
        this.role = role;
    }

    public boolean hasPermission(Permission permission) {
        return role != null && role.hasPermission(permission);
    }
}

