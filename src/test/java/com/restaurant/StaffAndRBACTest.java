package com.restaurant;

import com.restaurant.enums.Department;
import com.restaurant.enums.Permission;
import com.restaurant.model.Chef;
import com.restaurant.model.Role;
import com.restaurant.model.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff & RBAC Permissions Test Suite")
class StaffAndRBACTest {

    @Test
    @DisplayName("Should assign permissions to role and verify authorization")
    void testRolePermissions() {
        Role headChefRole = new Role("ROLE-01", "Head Chef", Department.KITCHEN,
                Set.of(Permission.PREPARE_FOOD, Permission.APPROVE_RECIPE, Permission.MANAGE_MENU));

        assertTrue(headChefRole.hasPermission(Permission.PREPARE_FOOD));
        assertTrue(headChefRole.hasPermission(Permission.APPROVE_RECIPE));
        assertFalse(headChefRole.hasPermission(Permission.GENERATE_BILL));
    }

    @Test
    @DisplayName("Should instantiate Waiter and Chef with respective departments")
    void testStaffCreation() {
        Role waiterRole = new Role("ROLE-02", "Senior Waiter", Department.SERVICE,
                Set.of(Permission.TAKE_ORDER, Permission.GENERATE_BILL));
        Waiter waiter = new Waiter("W-01", "Ramesh Kumar", "ramesh@restaurant.com", "9876543210", waiterRole);

        assertEquals("Ramesh Kumar", waiter.getName());
        assertEquals(Department.SERVICE, waiter.getDepartment());
        assertTrue(waiter.canTakeOrder());
        assertTrue(waiter.canGenerateBill());

        Role commisChefRole = new Role("ROLE-03", "Commis Chef", Department.KITCHEN,
                Set.of(Permission.PREPARE_FOOD));
        Chef chef = new Chef("C-01", "Sanjeev", "sanjeev@restaurant.com", "9876543211", commisChefRole);

        assertEquals(Department.KITCHEN, chef.getDepartment());
        assertTrue(chef.canPrepareFood());
        assertFalse(chef.canApproveRecipe());
    }

    @Test
    @DisplayName("Should throw exception if employee role department mismatches employee department")
    void testDepartmentMismatchThrowsException() {
        Role kitchenRole = new Role("ROLE-04", "Line Cook", Department.KITCHEN, Set.of(Permission.PREPARE_FOOD));

        assertThrows(IllegalArgumentException.class, () ->
                new Waiter("W-02", "Invalid Waiter", "test@test.com", "1234567890", kitchenRole));
    }
}

