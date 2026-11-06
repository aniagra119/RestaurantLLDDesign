package com.restaurant.model;

import com.restaurant.enums.Department;
import com.restaurant.enums.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Waiter extends Employee {
    private final List<String> assignedTables = new ArrayList<>();

    public Waiter(String id, String name, String email, String phoneNumber, Role role) {
        super(id, name, email, phoneNumber, Department.SERVICE, role);
    }

    public void assignTable(String tableNumber) {
        if (!assignedTables.contains(tableNumber)) {
            assignedTables.add(tableNumber);
        }
    }

    public void unassignTable(String tableNumber) {
        assignedTables.remove(tableNumber);
    }

    public List<String> getAssignedTables() {
        return Collections.unmodifiableList(assignedTables);
    }

    public boolean canTakeOrder() {
        return hasPermission(Permission.TAKE_ORDER);
    }

    public boolean canGenerateBill() {
        return hasPermission(Permission.GENERATE_BILL);
    }
}

