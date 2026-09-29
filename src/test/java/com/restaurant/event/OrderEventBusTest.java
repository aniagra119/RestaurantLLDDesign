package com.restaurant.event;

import com.restaurant.enums.KitchenStation;
import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.WaiterLevel;
import com.restaurant.event.listener.CustomerNotificationListener;
import com.restaurant.event.listener.KitchenAuditLogListener;
import com.restaurant.event.listener.KitchenDisplayListener;
import com.restaurant.model.Customer;
import com.restaurant.model.CustomerVisit;
import com.restaurant.model.Order;
import com.restaurant.model.Table;
import com.restaurant.model.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Event Bus & Observer Pattern Tests")
class OrderEventBusTest {

    @Test
    @DisplayName("Should publish events to subscribed listeners correctly")
    void testEventPublishing() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        KitchenDisplayListener kitchenListener = new KitchenDisplayListener(KitchenStation.HOT_KITCHEN);
        CustomerNotificationListener customerListener = new CustomerNotificationListener();
        KitchenAuditLogListener auditListener = new KitchenAuditLogListener();

        publisher.subscribe(kitchenListener);
        publisher.subscribe(customerListener);
        publisher.subscribe(auditListener);
        assertEquals(3, publisher.getListeners().size());

        Customer customer = new Customer("CUST-1", "Anirudh", "9999988888", "anirudh@gmail.com");
        Table table = new Table("T-5", 4);
        Waiter waiter = new Waiter("W-1", "Karan", "karan@restaurant.com", "9876500000", WaiterLevel.SENIOR);
        CustomerVisit visit = new CustomerVisit("VISIT-1", customer, table, waiter);

        Order order = new Order("ORD-999");
        publisher.publishOrderPlaced(new OrderPlacedEvent("EVT-1", visit, order));

        assertEquals(1, kitchenListener.getRoutedOrders().size());
        assertEquals("ORD-999", kitchenListener.getRoutedOrders().get(0));

        assertEquals(1, customerListener.getNotifications().size());
        assertTrue(customerListener.getNotifications().get(0).contains("Anirudh"));

        assertEquals(1, auditListener.getAuditLogs().size());

        // Status transition
        publisher.publishStatusChanged(new OrderStatusChangedEvent("EVT-2", order, OrderStatus.PLACED, OrderStatus.PREPARING));
        assertEquals(2, customerListener.getNotifications().size());
        assertEquals(2, auditListener.getAuditLogs().size());

        // Unsubscribe
        publisher.unsubscribe(kitchenListener);
        assertEquals(2, publisher.getListeners().size());
    }

    @Test
    @DisplayName("Should handle multiple kitchen station displays independently")
    void testMultipleStationDisplays() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        KitchenDisplayListener hotKitchen = new KitchenDisplayListener(KitchenStation.HOT_KITCHEN);
        KitchenDisplayListener bar = new KitchenDisplayListener(KitchenStation.BAR);
        KitchenDisplayListener dessert = new KitchenDisplayListener(KitchenStation.DESSERT_STATION);

        publisher.subscribe(hotKitchen);
        publisher.subscribe(bar);
        publisher.subscribe(dessert);

        assertEquals(KitchenStation.HOT_KITCHEN, hotKitchen.getStation());
        assertEquals(KitchenStation.BAR, bar.getStation());
        assertEquals(KitchenStation.DESSERT_STATION, dessert.getStation());
        assertEquals(3, publisher.getListeners().size());
    }

    @Test
    @DisplayName("Should ignore subscribing duplicate listeners")
    void testDuplicateSubscription() {
        OrderEventPublisher publisher = new OrderEventPublisher();
        KitchenAuditLogListener listener = new KitchenAuditLogListener();

        publisher.subscribe(listener);
        publisher.subscribe(listener);
        assertEquals(1, publisher.getListeners().size());
    }
}
