package com.restaurant;

import com.restaurant.billing.Bill;
import com.restaurant.billing.Payment;
import com.restaurant.billing.strategy.StandardBillingStrategy;
import com.restaurant.builder.MenuBuilder;
import com.restaurant.builder.RecipeBuilder;
import com.restaurant.builder.RestaurantBuilder;
import com.restaurant.customization.BaseDishItem;
import com.restaurant.customization.ExtraToppingDecorator;
import com.restaurant.customization.OrderableItem;
import com.restaurant.customization.SpiceLevelDecorator;
import com.restaurant.enums.ChefLevel;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.IngredientType;
import com.restaurant.enums.MeasurementUnit;
import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.PaymentMethod;
import com.restaurant.enums.VisitStatus;
import com.restaurant.enums.WaiterLevel;
import com.restaurant.event.OrderEventPublisher;
import com.restaurant.event.listener.KitchenDisplayListener;
import com.restaurant.enums.KitchenStation;
import com.restaurant.model.Address;
import com.restaurant.model.Chef;
import com.restaurant.model.Customer;
import com.restaurant.model.CustomerVisit;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.Menu;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Recipe;
import com.restaurant.model.Table;
import com.restaurant.model.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Restaurant End-to-End Integration Test")
class RestaurantIntegrationTest {

    @Test
    @DisplayName("Should execute end-to-end restaurant lifecycle seamlessly")
    void testEndToEndDiningLifecycle() {
        // 1. Setup ingredients (solids in grams, liquids in ml)
        Ingredient pastaDough = new Ingredient("ING-PST", "Semolina", IngredientType.SOLID, MeasurementUnit.GRAMS, 5000);
        Ingredient truffleOil = new Ingredient("ING-OIL", "Truffle Oil", IngredientType.LIQUID, MeasurementUnit.MILLILITERS, 1000);

        Recipe trufflePastaRecipe = new RecipeBuilder()
                .setId("REC-TRF-PST")
                .addInstruction("Boil handmade tagliatelle")
                .addInstruction("Drizzle with cold-pressed white truffle oil")
                .setPrepTimeMinutes(20)
                .addIngredient(pastaDough, 150, MeasurementUnit.GRAMS)
                .addIngredient(truffleOil, 10, MeasurementUnit.MILLILITERS)
                .build();

        Dish truffleTagliatelle = new Dish("DISH-TRF", "Truffle Tagliatelle", "Handcrafted pasta with white truffle",
                DietaryType.VEG, 650.0, trufflePastaRecipe, KitchenStation.HOT_KITCHEN);

        Menu menu = new MenuBuilder()
                .setMenuId("MENU-DINNER")
                .setName("Evening Degustation Menu")
                .addDishToSection("Pastas & Mains", truffleTagliatelle)
                .build();

        Table table4 = new Table("T-4", 4);
        Waiter waiterJohn = new Waiter("W-10", "John Doe", "john@bistro.com", "9988776655", WaiterLevel.SENIOR);
        Chef chefPierre = new Chef("C-01", "Pierre Gagnaire", "pierre@bistro.com", "9988776600", ChefLevel.EXECUTIVE_CHEF, KitchenStation.HOT_KITCHEN);

        OrderEventPublisher eventPublisher = new OrderEventPublisher();
        KitchenDisplayListener hotKitchenKds = new KitchenDisplayListener(KitchenStation.HOT_KITCHEN);
        eventPublisher.subscribe(hotKitchenKds);

        Restaurant bistro = new RestaurantBuilder()
                .setName("Bistro Lumiere")
                .setAddress(new Address("12 Rue Saint-Honore", "Paris", "Ile-de-France", "75001"))
                .addCuisine(CuisineType.FRENCH)
                .setMenu(menu)
                .addTable(table4)
                .addWaiter(waiterJohn)
                .addChef(chefPierre)
                .setEventPublisher(eventPublisher)
                .build();

        // 2. Customer arrives & seated
        Customer customer = new Customer("CUST-100", "Alice Martin", "9812345678", "alice@paris.fr");
        CustomerVisit visit = bistro.hostGuest(customer, table4, waiterJohn);
        assertNotNull(visit);
        assertTrue(table4.isOccupied());
        assertTrue(waiterJohn.getAssignedTables().contains(table4));

        // 3. Multi-round ordering
        // Round 1: Customized Pasta (Base + Extra Truffle Shavings + Mild Spice)
        OrderableItem customizedPasta = new BaseDishItem(truffleTagliatelle);
        customizedPasta = new ExtraToppingDecorator(customizedPasta, "Fresh Black Truffle Shavings", 150.0);
        customizedPasta = new SpiceLevelDecorator(customizedPasta, "Mild");

        assertEquals(800.0, customizedPasta.getPrice()); // 650 + 150

        Order round1 = new Order("ORD-001");
        round1.addItem(new OrderItem("ITEM-01", customizedPasta, 2)); // 1600.0
        bistro.placeOrder(visit.getVisitId(), round1);

        assertEquals(1, hotKitchenKds.getRoutedOrders().size());
        assertEquals("ORD-001", hotKitchenKds.getRoutedOrders().get(0));

        // 4. Kitchen processes order & stock is deducted
        bistro.getKitchenService().startPreparingOrder(round1, chefPierre);
        assertEquals(OrderStatus.PREPARING, round1.getStatus());
        assertEquals(4700, pastaDough.getCurrentStock()); // 5000 - (150 * 2) = 4700
        assertEquals(980, truffleOil.getCurrentStock());  // 1000 - (10 * 2) = 980

        bistro.getKitchenService().markOrderReady(round1);
        bistro.getKitchenService().serveOrder(round1);
        assertEquals(OrderStatus.SERVED, round1.getStatus());

        // 5. Generate Bill: Subtotal = 1600. Tax = 80 (5%), Service = 160 (10%) -> Total = 1840.0
        Bill bill = bistro.generateBill(visit.getVisitId(), new StandardBillingStrategy(0.05, 0.10));
        assertEquals(1600.0, bill.getItemSubtotal());
        assertEquals(1840.0, bill.getTotalPayable());
        assertEquals(VisitStatus.BILLED, visit.getStatus());

        // 6. Split Payment: Alice pays ₹1000 via Card, companion pays ₹840 via UPI
        visit.makePayment(new Payment("PAY-01", 1000.0, PaymentMethod.CREDIT_CARD, "TXN-CARD-1"));
        assertFalse(bill.isFullyPaid());
        assertEquals(840.0, bill.getRemainingAmount());

        visit.makePayment(new Payment("PAY-02", 840.0, PaymentMethod.UPI, "TXN-UPI-2"));
        assertTrue(bill.isFullyPaid());

        // 7. Complete Visit
        bistro.getVisitManager().endVisit(visit.getVisitId());
        assertEquals(VisitStatus.COMPLETED, visit.getStatus());
        assertFalse(table4.isOccupied());
        assertFalse(waiterJohn.getAssignedTables().contains(table4));
    }
}
