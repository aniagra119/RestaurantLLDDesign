package com.restaurant.model;

import com.restaurant.enums.ChefLevel;
import com.restaurant.enums.KitchenStation;
import com.restaurant.enums.WaiterLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Staff Hierarchy & Levels Test")
class StaffAndLevelTest {

    @Test
    @DisplayName("Should maintain Waiter details and WaiterLevel properly")
    void testWaiterLevelsAndTableAssignment() {
        Waiter juniorWaiter = new Waiter("W-1", "Rahul Sharma", "rahul@restaurant.com", "9876543210", WaiterLevel.JUNIOR);
        assertEquals(WaiterLevel.JUNIOR, juniorWaiter.getLevel());
        assertEquals("Rahul Sharma", juniorWaiter.getName());
        assertEquals("rahul@restaurant.com", juniorWaiter.getEmail());
        assertEquals("9876543210", juniorWaiter.getPhoneNumber());
        assertTrue(juniorWaiter.getAssignedTables().isEmpty());

        Table table1 = new Table("T-1", 2);
        Table table2 = new Table("T-2", 4);

        juniorWaiter.assignTable(table1);
        juniorWaiter.assignTable(table2);
        assertEquals(2, juniorWaiter.getAssignedTables().size());

        // Re-assigning same table should be idempotent
        juniorWaiter.assignTable(table1);
        assertEquals(2, juniorWaiter.getAssignedTables().size());

        juniorWaiter.releaseTable(table1);
        assertEquals(1, juniorWaiter.getAssignedTables().size());
        assertTrue(juniorWaiter.getAssignedTables().contains(table2));

        // Releasing unassigned table should not throw
        juniorWaiter.releaseTable(table1);
        assertEquals(1, juniorWaiter.getAssignedTables().size());
    }

    @Test
    @DisplayName("Should verify all WaiterLevel enum values")
    void testAllWaiterLevels() {
        WaiterLevel[] levels = WaiterLevel.values();
        assertEquals(3, levels.length);
        assertNotNull(WaiterLevel.valueOf("JUNIOR"));
        assertNotNull(WaiterLevel.valueOf("SENIOR"));
        assertNotNull(WaiterLevel.valueOf("MANAGER"));
    }

    @Test
    @DisplayName("Should maintain Chef details and ChefLevel properly")
    void testChefLevelsAndStations() {
        Chef commis = new Chef("C-1", "Aman Verma", "aman@restaurant.com", "9123456780", ChefLevel.COMMIS_CHEF, KitchenStation.HOT_KITCHEN);
        assertEquals(ChefLevel.COMMIS_CHEF, commis.getLevel());
        assertEquals(KitchenStation.HOT_KITCHEN, commis.getPrimaryStation());

        Chef sousChef = new Chef("C-2", "Pooja Hegde", "pooja@restaurant.com", "9123456781", ChefLevel.SOUS_CHEF, KitchenStation.COLD_STATION);
        assertEquals(ChefLevel.SOUS_CHEF, sousChef.getLevel());
        assertEquals(KitchenStation.COLD_STATION, sousChef.getPrimaryStation());

        Chef headChef = new Chef("C-3", "Sanjeev Kapoor", "sanjeev@restaurant.com", "9123456782", ChefLevel.HEAD_CHEF);
        assertEquals(ChefLevel.HEAD_CHEF, headChef.getLevel());
        assertEquals(KitchenStation.HOT_KITCHEN, headChef.getPrimaryStation()); // Default station

        Chef execChef = new Chef("C-4", "Gordon Ramsay", "gordon@restaurant.com", "9123456783", ChefLevel.EXECUTIVE_CHEF);
        assertEquals(ChefLevel.EXECUTIVE_CHEF, execChef.getLevel());
    }

    @Test
    @DisplayName("Should verify all ChefLevel enum values")
    void testAllChefLevels() {
        ChefLevel[] levels = ChefLevel.values();
        assertEquals(4, levels.length);
        assertNotNull(ChefLevel.valueOf("COMMIS_CHEF"));
        assertNotNull(ChefLevel.valueOf("SOUS_CHEF"));
        assertNotNull(ChefLevel.valueOf("HEAD_CHEF"));
        assertNotNull(ChefLevel.valueOf("EXECUTIVE_CHEF"));
    }

    @Test
    @DisplayName("Should verify Employee equals and hashCode identity")
    void testEmployeeEquality() {
        Waiter w1 = new Waiter("EMP-1", "Sameer", "s@test.com", "9800000001", WaiterLevel.JUNIOR);
        Waiter w2 = new Waiter("EMP-1", "Sameer Verma", "sv@test.com", "9800000001", WaiterLevel.SENIOR);
        Chef c1 = new Chef("EMP-2", "Chef Ajay", "a@test.com", "9800000002", ChefLevel.HEAD_CHEF);

        assertEquals(w1, w2);
        assertEquals(w1.hashCode(), w2.hashCode());
        assertNotEquals(w1, c1);
    }
}
