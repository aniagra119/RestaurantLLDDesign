package com.restaurant.model;

import com.restaurant.enums.Department;
import com.restaurant.enums.Permission;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Role {
    private final String id;
    private final String name;
    private final Department department;
    private final Set<Permission> permissions;

    public Role(String id, String name, Department department, Set<Permission> permissions) {
        this.id = Objects.requireNonNull(id, "Role ID must not be null");
        this.name = Objects.requireNonNull(name, "Role name must not be null");
        this.department = Objects.requireNonNull(department, "Department must not be null");
        this.permissions = permissions != null ? new HashSet<>(permissions) : new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Department getDepartment() {
        return department;
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }
}

