// ============================================================================
// RELATIONSHIPS & STRUCTURAL CONTRACTS
// ============================================================================
// - Restaurant has-a Address (Composition)
// - Restaurant has-a Menu (Composition)
// - Restaurant has-a List<Waiter> (Aggregation)
// - Restaurant has-a List<Chef> (Aggregation)
// - Restaurant has-a List<CuisineType> (Composition, Enum)
// - Restaurant has-a List<Table> (Composition)
// - Restaurant has-a List<CustomerVisit> (Composition, active & historical dining sessions)
// - Menu has-a List<MenuSection> (Composition)
// - MenuSection has-a List<Dish> (Composition)
// - Dish has-a DietaryType (Composition, Enum: VEG, NON_VEG, VEGAN)
// - Dish has-a Recipe (Composition)
// - Recipe has-a List<RecipeIngredient> (Composition)
// - RecipeIngredient has-a Ingredient (Aggregation)
// - RecipeIngredient has-a MeasurementUnit (Composition, Enum: GRAMS, MILLILITERS)
// - Ingredient has-a IngredientType (Composition, Enum: SOLID, LIQUID)
// - Employee is base class for Waiter and Chef (Inheritance / Polymorphism)
// - Waiter has-a WaiterLevel (Composition, Enum: JUNIOR, SENIOR, MANAGER)
// - Chef has-a ChefLevel (Composition, Enum: COMMIS_CHEF, SOUS_CHEF, HEAD_CHEF, EXECUTIVE_CHEF)
// - CustomerVisit has-a Customer (Aggregation)
// - CustomerVisit has-a Table (Aggregation)
// - CustomerVisit has-a Waiter (Aggregation, assigned table attendant)
// - CustomerVisit has-a List<Order> (Composition, tracks multi-round dining orders)
// - CustomerVisit has-a Bill (Composition, terminal settlement bill)
// - CustomerVisit has-a VisitStatus (Composition, Enum: ACTIVE, BILLED, COMPLETED)
// - Order has-a List<OrderItem> (Composition)
// - Order has-a OrderStatus (Composition, Enum: PLACED, PREPARING, READY, SERVED, CANCELLED)
// - OrderItem has-a OrderableItem (Aggregation via Decorator Pattern)
// - OrderableItem is interface implemented by BaseDishItem and DishCustomizationDecorator (Decorator Pattern)
// - DishCustomizationDecorator is abstract base class for ExtraToppingDecorator and SpiceLevelDecorator (Decorator Pattern)
// - Bill has-a List<Payment> (Composition, split-payment ledger)
// - Bill has-a BillingStrategy (Aggregation via Strategy Pattern)
// - Bill has-a BillStatus (Composition, Enum: UNPAID, PARTIALLY_PAID, PAID)
// - Payment has-a PaymentMethod (Composition, Enum: CASH, CREDIT_CARD, DEBIT_CARD, UPI)
// - BillingStrategy is interface implemented by StandardBillingStrategy and DiscountedBillingStrategy (Strategy Pattern)
// - Restaurant has-a OrderEventPublisher (Composition via Observer Pattern)
// - OrderEventPublisher has-a List<OrderEventListener> (Aggregation via Observer Pattern)
// - OrderEventListener is interface implemented by KitchenDisplayListener and CustomerNotificationListener (Observer Pattern)

// ============================================================================
// ENUMS
// ============================================================================

enum CuisineType {
    ITALIAN,
    FRENCH,
    CHINESE,
    JAPANESE,
    INDIAN,
    CONTINENTAL,
    MEXICAN
}

enum DietaryType {
    VEG,
    NON_VEG,
    VEGAN
}

enum IngredientType {
    SOLID,
    LIQUID
}

enum MeasurementUnit {
    GRAMS,
    MILLILITERS
}

enum WaiterLevel {
    JUNIOR,
    SENIOR,
    MANAGER
}

enum ChefLevel {
    COMMIS_CHEF,
    SOUS_CHEF,
    HEAD_CHEF,
    EXECUTIVE_CHEF
}

enum OrderStatus {
    PLACED,
    PREPARING,
    READY,
    SERVED,
    CANCELLED
}

enum VisitStatus {
    ACTIVE,
    BILLED,
    COMPLETED,
    CANCELLED
}

enum BillStatus {
    UNPAID,
    PARTIALLY_PAID,
    PAID,
    REFUNDED
}

enum PaymentMethod {
    CASH,
    CREDIT_CARD,
    DEBIT_CARD,
    UPI
}

// ============================================================================
// GEOGRAPHY & ACTORS
// ============================================================================

class Address {
    - String street
    - String city
    - String state
    - String postalCode

    + Address(String street, String city, String state, String postalCode)
    + String getStreet()
    + String getCity()
    + String getState()
    + String getPostalCode()
    + String getFormattedAddress()
}

class Customer {
    - String id
    - String name
    - String phoneNumber
    - String email

    + Customer(String id, String name, String phoneNumber, String email)
    + String getId()
    + String getName()
    + String getPhoneNumber()
    + String getEmail()
}

class Table {
    - String tableNumber
    - int capacity
    - boolean isOccupied

    + Table(String tableNumber, int capacity)
    + String getTableNumber()
    + int getCapacity()
    + boolean isOccupied()
    + void markOccupied()
    + void markAvailable()
}

abstract class Employee {
    # String id
    # String name
    # String email
    # String phoneNumber

    + Employee(String id, String name, String email, String phoneNumber)
    + String getId()
    + String getName()
    + String getEmail()
    + String getPhoneNumber()
}

class Waiter extends Employee {
    - WaiterLevel level
    - List<Table> assignedTables

    + Waiter(String id, String name, String email, String phoneNumber, WaiterLevel level)
    + WaiterLevel getLevel()
    + List<Table> getAssignedTables()
    + void assignTable(Table table)
    + void releaseTable(Table table)
}

class Chef extends Employee {
    - ChefLevel level

    + Chef(String id, String name, String email, String phoneNumber, ChefLevel level)
    + ChefLevel getLevel()
    + void prepareRecipe(Recipe recipe)
}

// ============================================================================
// INVENTORY, RECIPES & DISHES
// ============================================================================

class Ingredient {
    - String id
    - String name
    - IngredientType type
    - MeasurementUnit unit
    - double currentStock

    + Ingredient(String id, String name, IngredientType type, MeasurementUnit unit, double currentStock)
    + String getId()
    + String getName()
    + IngredientType getType()
    + MeasurementUnit getUnit()
    + double getCurrentStock()
    + void deductStock(double amount)
    + void addStock(double amount)
}

class RecipeIngredient {
    - Ingredient ingredient
    - double amount
    - MeasurementUnit unit

    + RecipeIngredient(Ingredient ingredient, double amount, MeasurementUnit unit)
    + Ingredient getIngredient()
    + double getAmount()
    + MeasurementUnit getUnit()
}

class Recipe {
    - String id
    - List<String> instructions
    - int prepTimeMinutes
    - List<RecipeIngredient> ingredients

    + Recipe(String id, List<String> instructions, int prepTimeMinutes, List<RecipeIngredient> ingredients)
    + String getId()
    + List<String> getInstructions()
    + int getPrepTimeMinutes()
    + List<RecipeIngredient> getIngredients()
}

class Dish {
    - String id
    - String name
    - String description
    - DietaryType type
    - double price
    - Recipe recipe
    - boolean isAvailable

    + Dish(String id, String name, String description, DietaryType type, double price, Recipe recipe)
    + String getId()
    + String getName()
    + String getDescription()
    + DietaryType getType()
    + double getPrice()
    + Recipe getRecipe()
    + boolean isAvailable()
    + void setAvailable(boolean available)
}

class MenuSection {
    - String sectionName
    - List<Dish> dishes

    + MenuSection(String sectionName, List<Dish> dishes)
    + String getSectionName()
    + List<Dish> getDishes()
    + void addDish(Dish dish)
}

class Menu {
    - String menuId
    - String name
    - List<MenuSection> sections

    + Menu(String menuId, String name, List<MenuSection> sections)
    + String getMenuId()
    + String getName()
    + List<MenuSection> getSections()
    + void addSection(MenuSection section)
    + Dish findDishByName(String name)
}

// ============================================================================
// CUSTOMIZABLE DISH ITEMS (Decorator Pattern)
// ============================================================================

interface OrderableItem {
    + String getName()
    + double getPrice()
    + DietaryType getDietaryType()
}

class BaseDishItem implements OrderableItem {
    - Dish dish

    + BaseDishItem(Dish dish)
    + String getName()
    + double getPrice()
    + DietaryType getDietaryType()
    + Dish getDish()
}

abstract class DishCustomizationDecorator implements OrderableItem {
    # OrderableItem wrappedItem

    + DishCustomizationDecorator(OrderableItem wrappedItem)
    + String getName()
    + double getPrice()
    + DietaryType getDietaryType()
}

class ExtraToppingDecorator extends DishCustomizationDecorator {
    - String toppingName
    - double additionalCost

    + ExtraToppingDecorator(OrderableItem wrappedItem, String toppingName, double additionalCost)
    + String getName()
    + double getPrice()
    + String getToppingName()
}

class SpiceLevelDecorator extends DishCustomizationDecorator {
    - String spiceLevel

    + SpiceLevelDecorator(OrderableItem wrappedItem, String spiceLevel)
    + String getName()
    + double getPrice()
    + String getSpiceLevel()
}

// ============================================================================
// ORDERS & MULTI-ROUND DINING VISITS
// ============================================================================

class OrderItem {
    - String itemId
    - OrderableItem item
    - int quantity
    - String specialInstructions

    + OrderItem(String itemId, OrderableItem item, int quantity, String specialInstructions)
    + String getItemId()
    + OrderableItem getItem()
    + int getQuantity()
    + String getSpecialInstructions()
    + double getSubtotal()
}

class Order {
    - String orderId
    - List<OrderItem> items
    - OrderStatus status
    - long placedTimestamp

    + Order(String orderId)
    + String getOrderId()
    + List<OrderItem> getItems()
    + OrderStatus getStatus()
    + long getPlacedTimestamp()
    + void addItem(OrderItem item)
    + void updateStatus(OrderStatus status)
    + double calculateSubtotal()
}

class CustomerVisit {
    - String visitId
    - Customer customer
    - Table table
    - Waiter assignedWaiter
    - List<Order> orders
    - Bill bill
    - VisitStatus status
    - long startTime
    - long endTime

    + CustomerVisit(String visitId, Customer customer, Table table, Waiter assignedWaiter)
    + String getVisitId()
    + Customer getCustomer()
    + Table getTable()
    + Waiter getAssignedWaiter()
    + List<Order> getOrders()
    + Bill getBill()
    + VisitStatus getStatus()
    + void addOrder(Order order)
    + void setBill(Bill bill)
    + void completeVisit()
}

// ============================================================================
// BILLING, PAYMENTS & STRATEGY PATTERN
// ============================================================================

class Payment {
    - String paymentId
    - double amount
    - PaymentMethod method
    - String transactionReference
    - long timestamp

    + Payment(String paymentId, double amount, PaymentMethod method, String transactionReference)
    + String getPaymentId()
    + double getAmount()
    + PaymentMethod getMethod()
    + String getTransactionReference()
    + long getTimestamp()
}

class Bill {
    - String billId
    - double itemSubtotal
    - double taxAmount
    - double serviceCharge
    - double discountAmount
    - double totalPayable
    - BillStatus status
    - List<Payment> payments

    + Bill(String billId, double itemSubtotal, double taxAmount, double serviceCharge, double discountAmount, double totalPayable)
    + String getBillId()
    + double getItemSubtotal()
    + double getTaxAmount()
    + double getServiceCharge()
    + double getDiscountAmount()
    + double getTotalPayable()
    + BillStatus getStatus()
    + List<Payment> getPayments()
    + void addPayment(Payment payment)
    + double getRemainingAmount()
    + boolean isFullyPaid()
}

interface BillingStrategy {
    + Bill calculateBill(String billId, List<Order> orders)
}

class StandardBillingStrategy implements BillingStrategy {
    - double taxRate
    - double serviceChargeRate

    + StandardBillingStrategy(double taxRate, double serviceChargeRate)
    + Bill calculateBill(String billId, List<Order> orders)
}

class DiscountedBillingStrategy implements BillingStrategy {
    - double taxRate
    - double serviceChargeRate
    - double discountPercentage

    + DiscountedBillingStrategy(double taxRate, double serviceChargeRate, double discountPercentage)
    + Bill calculateBill(String billId, List<Order> orders)
}

// ============================================================================
// EVENT NOTIFICATIONS (Observer Pattern)
// ============================================================================

class OrderPlacedEvent {
    - CustomerVisit visit
    - Order order
    - long timestamp

    + OrderPlacedEvent(CustomerVisit visit, Order order)
    + CustomerVisit getVisit()
    + Order getOrder()
    + long getTimestamp()
}

interface OrderEventListener {
    + void onOrderPlaced(OrderPlacedEvent event)
}

class KitchenDisplayListener implements OrderEventListener {
    + KitchenDisplayListener()
    + void onOrderPlaced(OrderPlacedEvent event)
    + void displayTicket(Order order)
}

class CustomerNotificationListener implements OrderEventListener {
    + CustomerNotificationListener()
    + void onOrderPlaced(OrderPlacedEvent event)
    + void sendOrderConfirmation(Customer customer, Order order)
}

class OrderEventPublisher {
    - List<OrderEventListener> listeners

    + OrderEventPublisher()
    + void subscribe(OrderEventListener listener)
    + void unsubscribe(OrderEventListener listener)
    + void notifyOrderPlaced(OrderPlacedEvent event)
}

// ============================================================================
// RESTAURANT ROOT AGGREGATE
// ============================================================================

class Restaurant {
    - String name
    - Address address
    - List<CuisineType> cuisines
    - Menu menu
    - List<Waiter> waiters
    - List<Chef> chefs
    - List<Table> tables
    - List<CustomerVisit> visits
    - OrderEventPublisher eventPublisher

    + Restaurant(String name, Address address, List<CuisineType> cuisines, Menu menu)
    + String getName()
    + Address getAddress()
    + List<CuisineType> getCuisines()
    + Menu getMenu()
    + List<Waiter> getWaiters()
    + List<Chef> getChefs()
    + List<Table> getTables()
    + void addWaiter(Waiter waiter)
    + void addChef(Chef chef)
    + void addTable(Table table)
    + CustomerVisit startCustomerVisit(Customer customer, Table table, Waiter waiter)
    + void placeOrderForVisit(String visitId, Order order)
    + Bill generateBillForVisit(String visitId, BillingStrategy strategy)
    + void settlePayment(String visitId, Payment payment)
}

// ============================================================================
// DESIGN PATTERN JUSTIFICATION & ARCHITECTURAL SUMMARY
// ============================================================================
// 1. Decorator Pattern (Dish Customizations):
//    - OrderableItem interface is wrapped dynamically by DishCustomizationDecorator (ExtraToppingDecorator,
//      SpiceLevelDecorator) around BaseDishItem. Enables flexible culinary modifications without creating
//      rigid combinatorial subclasses (e.g. PastaWithExtraCheeseAndMildSpice).
//
// 2. Strategy Pattern (Dynamic Billing & Taxes):
//    - BillingStrategy encapsulates bill calculation rules (StandardBillingStrategy vs DiscountedBillingStrategy
//      or HappyHourBillingStrategy). Decouples customer orders and session management from seasonal discounts,
//      taxes, and service fee algorithms.
//
// 3. Observer Pattern (Order Lifecycle & Kitchen Display):
//    - OrderEventPublisher broadcasts OrderPlacedEvent to KitchenDisplayListener (KDS tickets for chefs) and
//      CustomerNotificationListener (order status SMS) without coupling the dining visit to notification sinks.
//
// 4. Multi-Round Dining Lifecycle (CustomerVisit & Orders):
//    - CustomerVisit acts as the active session entity tracking 1 Customer, 1 Table, 1 Waiter, and 1..* sequential
//      Orders across the dining experience. Preserves clean order history for terminal split-payment Bill generation.
//
// 5. Strict Ingredient Measurement & Recipe Modeling:
//    - RecipeIngredient maps Ingredient to MeasurementUnit (GRAMS for SOLID ingredients, MILLILITERS for
//      LIQUID ingredients), guaranteeing Chef precision and automated kitchen inventory stock depletion.
