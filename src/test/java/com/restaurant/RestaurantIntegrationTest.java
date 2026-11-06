package com.restaurant;

import com.restaurant.decorator.AddOnDecorator;
import com.restaurant.decorator.BaseDishItem;
import com.restaurant.decorator.PreparedItem;
import com.restaurant.enums.BillStatus;
import com.restaurant.enums.CuisineType;
import com.restaurant.enums.Department;
import com.restaurant.enums.DietaryType;
import com.restaurant.enums.OrderChannel;
import com.restaurant.enums.OrderStatus;
import com.restaurant.enums.PaymentMethod;
import com.restaurant.enums.Permission;
import com.restaurant.enums.SessionStatus;
import com.restaurant.enums.Unit;
import com.restaurant.model.Address;
import com.restaurant.model.Bill;
import com.restaurant.model.Chef;
import com.restaurant.model.Course;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Customer;
import com.restaurant.model.DiningSession;
import com.restaurant.model.Dish;
import com.restaurant.model.Ingredient;
import com.restaurant.model.Menu;
import com.restaurant.model.MenuCourse;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Payment;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;
import com.restaurant.model.Role;
import com.restaurant.model.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("End-to-End Restaurant Management System Integration Test")
class RestaurantIntegrationTest {

    @Test
    @DisplayName("Should execute complete dining lifecycle from seating, multi-round orders, kitchen prep, to split bill settlement")
    void testFullRestaurantLifecycle() {
        // 1. Initialize Restaurant with Address & Cuisines
        Address address = new Address("100 Feet Road, Indiranagar", "Bangalore", "Karnataka", "560038", "India");
        Menu menu = new Menu("MENU-DINEIN", "Artisan Dine-in Menu");
        Restaurant restaurant = new Restaurant("The Bangalore Trattoria", address, menu);

        Cuisine italian = new Cuisine("CUIS-01", "Italian", CuisineType.ITALIAN);
        Cuisine continental = new Cuisine("CUIS-02", "Continental", CuisineType.CONTINENTAL);
        restaurant.addCuisine(italian);
        restaurant.addCuisine(continental);

        // 2. Setup Kitchen Inventory
        Ingredient pasta = new Ingredient("ING-01", "Fettuccine Pasta", Unit.GRAM, 5000.0);
        Ingredient truffleOil = new Ingredient("ING-02", "White Truffle Oil", Unit.TABLESPOON, 100.0);
        Ingredient parmesan = new Ingredient("ING-03", "Parmigiano Reggiano", Unit.GRAM, 2000.0);

        // 3. Setup Recipes & Dishes
        Recipe fettuccineRecipe = new Recipe("REC-01", "Truffle Fettuccine",
                List.of("Boil fettuccine for 10 mins", "Toss with truffle oil and emulsified pasta water"),
                5, 10,
                List.of(
                        new RecipeIngredient(pasta, 150.0, Unit.GRAM),
                        new RecipeIngredient(truffleOil, 1.0, Unit.TABLESPOON)
                ));

        Dish truffleFettuccine = new Dish("DISH-01", "Truffle Fettuccine", "Handmade pasta in white truffle emulsion",
                DietaryType.VEG, italian, fettuccineRecipe);

        // 4. Setup Menu & Courses
        Course mains = new Course("CRS-MAINS", "Primi Piatti (Mains)");
        MenuCourse mainsCourse = new MenuCourse("MC-01", mains, 1);
        MenuItem fettuccineMenuItem = new MenuItem("MI-01", truffleFettuccine, BigDecimal.valueOf(650.00));
        mainsCourse.addItem(fettuccineMenuItem);
        menu.addMenuCourse(mainsCourse);

        // 5. Staff Onboarding (Waiters & Chefs with RBAC)
        Role serverRole = new Role("R-SERVER", "Floor Captain", Department.SERVICE,
                Set.of(Permission.TAKE_ORDER, Permission.GENERATE_BILL));
        Waiter waiter = new Waiter("W-01", "Devashish", "dev@trattoria.com", "9800011122", serverRole);
        restaurant.addWaiter(waiter);

        Role chefRole = new Role("R-CHEF", "Sous Chef", Department.KITCHEN,
                Set.of(Permission.PREPARE_FOOD, Permission.APPROVE_RECIPE));
        Chef chef = new Chef("C-01", "Chef Lorenzo", "lorenzo@trattoria.com", "9800033344", chefRole);
        restaurant.addChef(chef);

        // 6. Customer arrives and opens a Dining Session
        Customer customer = new Customer("CUST-99", "Anirudh Agrawal", "9999988888", "anirudh@tech.com");
        DiningSession session = restaurant.startDiningSession("SESS-BLR-01", "Table-4", customer, waiter);

        assertEquals(SessionStatus.ACTIVE, session.getStatus());
        assertTrue(waiter.getAssignedTables().contains("Table-4"));

        // 7. Multi-Round Ordering
        // Round 1: 2x Truffle Fettuccine with extra Parmesan cheese (+50.00 each)
        Order round1 = new Order("ORD-001", OrderChannel.DINE_IN);
        PreparedItem basePasta = new BaseDishItem(fettuccineMenuItem);
        RecipeIngredient extraCheese = new RecipeIngredient(parmesan, 30.0, Unit.GRAM);
        PreparedItem customizedPasta = new AddOnDecorator(basePasta, "Parmesan", BigDecimal.valueOf(50.00), extraCheese);

        assertEquals(BigDecimal.valueOf(700.00), customizedPasta.getPrice()); // 650 + 50 = 700.00
        round1.addItem(new OrderItem("OI-01", customizedPasta, 2, "Al dente please"));
        session.addOrder(round1);

        // 8. Kitchen Cook Execution (Verify Inventory Depletion!)
        double initialPasta = pasta.getAvailableQuantity();
        double initialOil = truffleOil.getAvailableQuantity();
        double initialCheese = parmesan.getAvailableQuantity();

        restaurant.cookOrder(round1, chef);

        assertEquals(OrderStatus.READY, round1.getStatus());
        // 2 portions cooked: 2 * 150g pasta = 300g deducted
        assertEquals(initialPasta - 300.0, pasta.getAvailableQuantity(), 0.001);
        // 2 portions cooked: 2 * 1 tbsp oil (15ml) = 30ml deducted
        assertEquals(initialOil - 30.0, truffleOil.getAvailableQuantity(), 0.001);
        // 2 portions cooked: 2 * 30g parmesan = 60g deducted
        assertEquals(initialCheese - 60.0, parmesan.getAvailableQuantity(), 0.001);

        restaurant.getKitchenService().markOrderServed(round1);
        assertEquals(OrderStatus.SERVED, round1.getStatus());

        // 9. Bill Generation & Split Payment
        // 2x 700.00 = 1400.00 Subtotal
        // GST (5%) = 70.00
        // Service Charge (10%) = 140.00
        // Total Payable = 1610.00
        Bill bill = session.generateBill(restaurant.getDefaultBillingStrategy());
        assertEquals(new BigDecimal("1400.00"), bill.getItemSubtotal());
        assertEquals(new BigDecimal("70.00"), bill.getTaxAmount());
        assertEquals(new BigDecimal("140.00"), bill.getServiceCharge());
        assertEquals(new BigDecimal("1610.00"), bill.getTotalPayable());
        assertEquals(BillStatus.UNPAID, bill.getBillStatus());

        // Split Payment:
        // Payment 1: ₹1000.00 via UPI
        Payment upiPayment = new Payment("PAY-01", new BigDecimal("1000.00"), PaymentMethod.UPI, "UPI-REF-9988");
        session.applyPayment(upiPayment);
        assertEquals(BillStatus.PARTIALLY_PAID, bill.getBillStatus());
        assertEquals(new BigDecimal("610.00"), bill.getRemainingAmount());

        // Payment 2: ₹610.00 via Credit Card
        Payment cardPayment = new Payment("PAY-02", new BigDecimal("610.00"), PaymentMethod.CREDIT_CARD, "CARD-AUTH-7766");
        session.applyPayment(cardPayment);
        assertEquals(BillStatus.PAID, bill.getBillStatus());
        assertTrue(bill.isFullyPaid());

        // 10. Session Settled & Closed
        assertEquals(SessionStatus.SETTLED, session.getStatus());
        assertNotNull(session.getClosedAt());

        restaurant.getSessionManager().closeSession(session.getSessionId());
        assertFalse(restaurant.getSessionManager().getSessionByTable("Table-4").isPresent());
        assertFalse(waiter.getAssignedTables().contains("Table-4"));
    }
}

