package com.restaurant.model;

import com.restaurant.enums.Department;
import com.restaurant.enums.Permission;

public class Chef extends Employee {

    public Chef(String id, String name, String email, String phoneNumber, Role role) {
        super(id, name, email, phoneNumber, Department.KITCHEN, role);
    }

    public boolean canPrepareFood() {
        return hasPermission(Permission.PREPARE_FOOD);
    }

    public boolean canApproveRecipe() {
        return hasPermission(Permission.APPROVE_RECIPE);
    }
}

