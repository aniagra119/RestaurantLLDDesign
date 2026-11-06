package com.restaurant;

import com.restaurant.decorator.BaseDishItem;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.KitchenStation;
import com.restaurant.enums.OrderChannel;
import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.Unit;
import com.restaurant.event.ItemPreparedEvent;
import com.restaurant.event.KitchenEventPublisher;
import com.restaurant.event.OrderCancelledEvent;
import com.restaurant.event.OrderPlacedEvent;
import com.restaurant.event.listener.AuditLogListener;
import com.restaurant.event.listener.BarStationListener;
import com.restaurant.event.listener.ColdStationListener;
import com.restaurant.event.listener.HotKitchenStationListener;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kitchen Display System (KDS) Event Bus Test Suite")
class KitchenEventBusTest {

    private KitchenEventPublisher eventPublisher;
    private HotKitchenStationListener hotKitchen;
    private ColdStationListener coldCounter;
    private BarStationListener barStation;
    private AuditLogListener auditLog;

    private MenuItem hotPizzaItem;
    private MenuItem coldSaladItem;
    private MenuItem barCocktailItem;

    @BeforeEach
    void setUp() {
        eventPublisher = new KitchenEventPublisher();
        hotKitchen = new HotKitchenStationListener();
        coldCounter = new ColdStationListener();
        barStation = new BarStationListener();
        auditLog = new AuditLogListener();

        eventPublisher.registerListener(hotKitchen);
        eventPublisher.registerListener(coldCounter);
        eventPublisher.registerListener(barStation);
        eventPublisher.registerListener(auditLog);

        Cuisine italian = new Cuisine("C-IT", "Italian", CuisineType.ITALIAN);
        Ingredient cheese = new Ingredient("ING-CH", "Mozzarella", Unit.GRAM, 10000.0);
        Ingredient lettuce = new Ingredient("ING-LT", "Romaine Lettuce", Unit.GRAM, 5000.0);
        Ingredient gin = new Ingredient("ING-GN", "London Dry Gin", Unit.MILLILITER, 5000.0);

        Dish pizza = new Dish("D-PZ", "Margherita Pizza", "Woodfired pizza", DietaryType.VEG, italian,
                new Recipe("R-PZ", "Pizza", List.of("Bake"), 10, 8, List.of(new RecipeIngredient(cheese, 150, Unit.GRAM))),
                KitchenStation.HOT_KITCHEN);

        Dish salad = new Dish("D-SL", "Caesar Salad", "Crisp lettuce and croutons", DietaryType.VEG, italian,
                new Recipe("R-SL", "Salad", List.of("Toss"), 5, 0, List.of(new RecipeIngredient(lettuce, 100, Unit.GRAM))),
                KitchenStation.COLD_COUNTER);

        Dish cocktail = new Dish("D-CK", "Negroni", "Classic aperitivo", DietaryType.VEGAN, italian,
                new Recipe("R-CK", "Negroni", List.of("Stir with ice"), 2, 0, List.of(new RecipeIngredient(gin, 45, Unit.MILLILITER))),
                KitchenStation.BAR_DISPENSE);

        hotPizzaItem = new MenuItem("MI-PZ", pizza, BigDecimal.valueOf(500.00));
        coldSaladItem = new MenuItem("MI-SL", salad, BigDecimal.valueOf(350.00));
        barCocktailItem = new MenuItem("MI-CK", cocktail, BigDecimal.valueOf(450.00));
    }

    @Test
    @DisplayName("Should route hot dish to Hot Kitchen station only")
    void testHotKitchenRouting() {
        Order order = new Order("ORD-HOT", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-1", new BaseDishItem(hotPizzaItem), 2, "Extra crispy"));

        eventPublisher.publish(new OrderPlacedEvent("SESS-1", "Table-4", order));

        assertEquals(1, hotKitchen.getActiveHotTickets().size());
        assertTrue(hotKitchen.getActiveHotTickets().getFirst().contains("2x Margherita Pizza"));
        assertTrue(hotKitchen.getActiveHotTickets().getFirst().contains("Table-4"));

        assertEquals(0, coldCounter.getActiveColdTickets().size());
        assertEquals(0, barStation.getActiveDrinkTickets().size());
        assertEquals(1, auditLog.getEventCount());
    }

    @Test
    @DisplayName("Should route salad to Cold Counter station only")
    void testColdCounterRouting() {
        Order order = new Order("ORD-COLD", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-2", new BaseDishItem(coldSaladItem), 1, "Dressing on the side"));

        eventPublisher.publish(new OrderPlacedEvent("SESS-1", "Table-2", order));

        assertEquals(0, hotKitchen.getActiveHotTickets().size());
        assertEquals(1, coldCounter.getActiveColdTickets().size());
        assertTrue(coldCounter.getActiveColdTickets().getFirst().contains("1x Caesar Salad"));
        assertEquals(0, barStation.getActiveDrinkTickets().size());
    }

    @Test
    @DisplayName("Should route drink to Bar Dispense station only")
    void testBarDispenseRouting() {
        Order order = new Order("ORD-BAR", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-3", new BaseDishItem(barCocktailItem), 3, ""));

        eventPublisher.publish(new OrderPlacedEvent("SESS-1", "Bar-Seat-1", order));

        assertEquals(0, hotKitchen.getActiveHotTickets().size());
        assertEquals(0, coldCounter.getActiveColdTickets().size());
        assertEquals(1, barStation.getActiveDrinkTickets().size());
        assertTrue(barStation.getActiveDrinkTickets().getFirst().contains("3x Negroni"));
    }

    @Test
    @DisplayName("Should simultaneously route mixed order across Hot, Cold, and Bar stations")
    void testMixedOrderRoutingAcrossAllStations() {
        Order order = new Order("ORD-MIX", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-10", new BaseDishItem(hotPizzaItem), 1, ""));
        order.addItem(new OrderItem("OI-11", new BaseDishItem(coldSaladItem), 1, ""));
        order.addItem(new OrderItem("OI-12", new BaseDishItem(barCocktailItem), 2, ""));

        eventPublisher.publish(new OrderPlacedEvent("SESS-1", "Table-9", order));

        assertEquals(1, hotKitchen.getActiveHotTickets().size());
        assertEquals(1, coldCounter.getActiveColdTickets().size());
        assertEquals(1, barStation.getActiveDrinkTickets().size());
        assertEquals(1, auditLog.getEventCount());
    }

    @Test
    @DisplayName("Should record item prepared and order cancelled events in audit log")
    void testAuditLogLifecycle() {
        eventPublisher.publish(new ItemPreparedEvent("ORD-1", "OI-1", "CHEF-01"));
        eventPublisher.publish(new OrderCancelledEvent("ORD-2", "Customer left"));

        assertEquals(2, auditLog.getEventCount());
        assertEquals("ITEM_PREPARED", auditLog.getEventLog().get(0).getEventType());
        assertEquals("ORDER_CANCELLED", auditLog.getEventLog().get(1).getEventType());
    }

    @Test
    @DisplayName("Should register and unregister event listeners safely")
    void testListenerRegistrationLifecycle() {
        assertEquals(4, eventPublisher.getListenerCount());

        eventPublisher.unregisterListener(hotKitchen);
        assertEquals(3, eventPublisher.getListenerCount());

        Order order = new Order("ORD-TEST", OrderChannel.DINE_IN);
        order.addItem(new OrderItem("OI-99", new BaseDishItem(hotPizzaItem), 1, ""));
        eventPublisher.publish(new OrderPlacedEvent("SESS-1", "Table-1", order));

        // Hot kitchen was unregistered, so it should receive zero tickets
        assertEquals(0, hotKitchen.getActiveHotTickets().size());
        // Audit log remains registered and receives event
        assertEquals(1, auditLog.getEventCount());
    }
}

