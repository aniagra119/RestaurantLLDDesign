package com.restaurant.service;

import com.restaurant.enums.ChefLevel;
import com.restaurant.enums.KitchenStation;
import com.restaurant.enums.WaiterLevel;
import com.restaurant.model.Chef;
import com.restaurant.model.Order;
import com.restaurant.model.Table;
import com.restaurant.model.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StaffManager & Chef Concurrency Capacity Tests")
class StaffManagerAndChefCapacityTest {

    @Test
    @DisplayName("Should assign least loaded waiter to balance table distribution")
    void testLoadBalancedWaiterAssignment() {
        StaffManager manager = new StaffManager();
        Waiter w1 = new Waiter("W-1", "Aman", "aman@test.com", "9800000001", WaiterLevel.JUNIOR);
        Waiter w2 = new Waiter("W-2", "Bina", "bina@test.com", "9800000002", WaiterLevel.SENIOR);

        manager.addWaiter(w1);
        manager.addWaiter(w2);

        Table t1 = new Table("T-1", 4);
        Table t2 = new Table("T-2", 4);
        Table t3 = new Table("T-3", 4);

        // Assign t1 -> assigned to w1 (both have 0, w1 first)
        Waiter assigned1 = manager.assignWaiter(t1);
        assertEquals(w1, assigned1);
        assertEquals(1, w1.getAssignedTables().size());

        // Assign t2 -> assigned to w2 (w2 has 0, w1 has 1)
        Waiter assigned2 = manager.assignWaiter(t2);
        assertEquals(w2, assigned2);
        assertEquals(1, w2.getAssignedTables().size());

        // Assign t3 -> assigned to w1 (tie-breaker)
        Waiter assigned3 = manager.assignWaiter(t3);
        assertEquals(w1, assigned3);
        assertEquals(2, w1.getAssignedTables().size());
    }

    @Test
    @DisplayName("Should enforce maximum concurrent orders per ChefLevel")
    void testChefCapacityLimits() {
        Chef commis = new Chef("C-COMMIS", "Rahul", "r@test.com", "9900000001", ChefLevel.COMMIS_CHEF);
        assertEquals(2, commis.getMaxConcurrentOrders());

        Order o1 = new Order("ORD-1");
        Order o2 = new Order("ORD-2");
        Order o3 = new Order("ORD-3");

        assertTrue(commis.canAcceptOrder());
        commis.acceptOrder(o1);
        assertEquals(commis, o1.getAssignedChef());
        assertEquals(1, commis.getCurrentOrders().size());

        assertTrue(commis.canAcceptOrder());
        commis.acceptOrder(o2);
        assertEquals(2, commis.getCurrentOrders().size());

        // At capacity (2)
        assertFalse(commis.canAcceptOrder());
        assertThrows(IllegalStateException.class, () -> commis.acceptOrder(o3));

        // Complete order 1 -> frees capacity
        commis.completeOrder(o1);
        assertEquals(1, commis.getCurrentOrders().size());
        assertTrue(commis.canAcceptOrder());

        // Now o3 can be accepted
        commis.acceptOrder(o3);
        assertEquals(2, commis.getCurrentOrders().size());
    }

    @Test
    @DisplayName("Should find available chef prioritizing target station match")
    void testFindAvailableChefWithStationPriority() {
        StaffManager manager = new StaffManager();
        Chef hotChef = new Chef("C-HOT", "Pierre", "p@test.com", "9900000010", ChefLevel.HEAD_CHEF, KitchenStation.HOT_KITCHEN);
        Chef barChef = new Chef("C-BAR", "Marco", "m@test.com", "9900000011", ChefLevel.SOUS_CHEF, KitchenStation.BAR);

        manager.addChef(hotChef);
        manager.addChef(barChef);

        Optional<Chef> foundHot = manager.findAvailableChef(KitchenStation.HOT_KITCHEN);
        assertTrue(foundHot.isPresent());
        assertEquals(hotChef, foundHot.get());

        Optional<Chef> foundBar = manager.findAvailableChef(KitchenStation.BAR);
        assertTrue(foundBar.isPresent());
        assertEquals(barChef, foundBar.get());
    }
}
