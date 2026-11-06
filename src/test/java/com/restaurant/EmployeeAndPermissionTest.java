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

@DisplayName("Employee, Role Mutation & Permission Boundary Test Suite")
class EmployeeAndPermissionTest {

    @Test
    @DisplayName("Should mutate permissions dynamically on Role")
    void testDynamicRolePermissionMutation() {
        Role role = new Role("R-TEST", "Trainee Server", Department.SERVICE, Set.of(Permission.TAKE_ORDER));
        assertTrue(role.hasPermission(Permission.TAKE_ORDER));
        assertFalse(role.hasPermission(Permission.GENERATE_BILL));

        // Promote role
        role.addPermission(Permission.GENERATE_BILL);
        assertTrue(role.hasPermission(Permission.GENERATE_BILL));

        // Demote role
        role.removePermission(Permission.GENERATE_BILL);
        assertFalse(role.hasPermission(Permission.GENERATE_BILL));
    }

    @Test
    @DisplayName("Should update employee role within same department and reject cross-department promotion")
    void testEmployeeRoleTransition() {
        Role juniorServerRole = new Role("R-JUNIOR", "Junior Server", Department.SERVICE, Set.of(Permission.TAKE_ORDER));
        Role seniorServerRole = new Role("R-SENIOR", "Head Captain", Department.SERVICE, Set.of(Permission.TAKE_ORDER, Permission.GENERATE_BILL));
        Role chefRole = new Role("R-CHEF", "Executive Chef", Department.KITCHEN, Set.of(Permission.PREPARE_FOOD, Permission.APPROVE_RECIPE));

        Waiter waiter = new Waiter("W-50", "Tarun", "tarun@test.com", "9900112233", juniorServerRole);
        assertFalse(waiter.hasPermission(Permission.GENERATE_BILL));

        // Valid promotion
        waiter.setRole(seniorServerRole);
        assertTrue(waiter.hasPermission(Permission.GENERATE_BILL));

        // Invalid cross-department role assignment
        assertThrows(IllegalArgumentException.class, () -> waiter.setRole(chefRole));
    }

    @Test
    @DisplayName("Should handle idempotent table assignment and unassignment on Waiter")
    void testWaiterTableAssignmentIdempotency() {
        Role role = new Role("R-W", "Server", Department.SERVICE, Set.of(Permission.TAKE_ORDER));
        Waiter waiter = new Waiter("W-51", "Kavita", "kavita@test.com", "9900112234", role);

        waiter.assignTable("Table-1");
        waiter.assignTable("Table-1"); // Duplicate assignment should be ignored
        assertEquals(1, waiter.getAssignedTables().size());

        waiter.assignTable("Table-2");
        assertEquals(2, waiter.getAssignedTables().size());

        waiter.unassignTable("Table-1");
        assertEquals(1, waiter.getAssignedTables().size());
        assertFalse(waiter.getAssignedTables().contains("Table-1"));
        assertTrue(waiter.getAssignedTables().contains("Table-2"));
    }
}

