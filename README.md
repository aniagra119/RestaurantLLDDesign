# Restaurant Management System — Low-Level Design (LLD)

[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge&logo=apachemaven&logoColor=white)](pom.xml)
[![Tests](https://img.shields.io/badge/Tests-40%20Passed-success?style=for-the-badge&logo=junit5&logoColor=white)](src/test/java/com/restaurant)
[![Design Patterns](https://img.shields.io/badge/Design_Patterns-GoF_%26_SOLID-blueviolet?style=for-the-badge)](DESIGN.md)

A production-grade, object-oriented Low-Level Design (LLD) and Machine Coding implementation of an enterprise **Restaurant Management System** in Java 21, built to the exact standards of [`ParkingLotLLDDesign`](https://github.com/aniagra119/ParkingLotLLDDesign) and [`ElevatorLLDDesign`](https://github.com/aniagra119/ElevatorLLDDesign). Adheres strictly to clean architecture, SOLID principles, and Gang of Four (GoF) design patterns.

---

## 1. System Requirements & Domain Mapping

| # | Requirement in Problem | Implementation Details | Design Pattern / Component |
| :---: | :--- | :--- | :--- |
| **1** | **Restaurant Aggregate** | Composes physical attributes, menus, tables, staff, and active visits. | `Restaurant` (**Facade Pattern**) |
| **2** | **Cuisines** | Strongly-typed cuisine classifications (`Italian`, `French`, `Chinese`, `Japanese`, `Indian`, `Continental`, `Mexican`). | `CuisineType` (Enum) |
| **3** | **Menu & Sections** | Menu contains dishes divided into logical sections (`Soups`, `Appetizers`, `Pastas & Mains`, `Desserts`). | `Menu`, `MenuSection` |
| **4** | **Dish & Price** | Dish defines name, description, dietary type (`VEG`, `NON_VEG`, `VEGAN`), price, target kitchen station, and recipe. | `Dish`, `DietaryType` |
| **5** | **Recipe & Ingredients** | Recipe details cooking instructions, prep time, and itemized ingredients. | `Recipe`, `RecipeIngredient` |
| **6** | **Ingredient Measurements** | Strict physical distinction between **Solids measured in GRAMS** and **Liquids measured in MILLILITERS**. | `Ingredient`, `IngredientType`, `MeasurementUnit` |
| **7** | **Waiter Hierarchy** | Explicit waiter levels (`Junior`, `Senior`, `Manager`) with dynamic table assignments. | `Waiter`, `WaiterLevel` |
| **8** | **Chef Hierarchy** | Explicit chef levels (`Commis Chef`, `Sous chef`, `Head Chef`, `Executive chef`) mapped to kitchen stations. | `Chef`, `ChefLevel`, `KitchenStation` |
| **9** | **Customizable Dishes** | Dynamic decoration for add-on toppings, spice levels, and protein additions without combinatorial class explosion. | `OrderableItem`, `BaseDishItem`, `DishCustomizationDecorator` (**Decorator Pattern**) |
| **10**| **Customer Visit & Multi-Round Orders** | Tracks dining guests across multi-round orders during their visit (`Round 1: Starters`, `Round 2: Mains`). | `CustomerVisit`, `Order`, `OrderItem` (**State Machine**) |
| **11**| **Kitchen Display System (KDS)** | Observer bus routing order tickets to station displays (`HOT_KITCHEN`, `COLD_STATION`, `BAR`, `DESSERT_STATION`). | `OrderEventPublisher`, `KitchenDisplayListener` (**Observer Pattern**) |
| **12**| **Kitchen Inventory Depletion** | Concurrency-safe atomic deduction of raw ingredient stocks upon kitchen order preparation. | `KitchenService`, `Ingredient.deductStock()` |
| **13**| **Itemized Billing & Strategies** | Configurable tax and service charge calculations with promotional/loyalty discount strategies. | `BillingStrategy`, `StandardBillingStrategy`, `DiscountedBillingStrategy` (**Strategy Pattern**) |
| **14**| **Split Payment Support** | Supports partial split payments across multiple payment methods (`CASH`, `UPI`, `CREDIT_CARD`, `DEBIT_CARD`). | `Bill`, `Payment`, `BillStatus` (`UNPAID` $\to$ `PARTIALLY_PAID` $\to$ `PAID`) |
| **15**| **Fluent Construction** | Step-by-step restaurant, menu, and recipe assembly. | `RestaurantBuilder`, `MenuBuilder`, `RecipeBuilder` (**Builder Pattern**) |

---

## 2. High-Level Architecture Topology

```mermaid
graph TD
    Client([Customer / Waiter / Hostess]) -->|Interacts with| R[Restaurant Facade]
    Builder[RestaurantBuilder] -->|Assembles| R

    subgraph Facade & Orchestration Subsystems
        R --> VM[VisitManager - Active Tables & Dining Visits]
        R --> KS[KitchenService - Recipe Prep & Inventory]
        R --> M[Menu - Sections & Dishes]
        R --> EP[OrderEventPublisher - KDS Event Bus]
    end

    subgraph Staff Hierarchy
        R --> W[Waiter - Junior / Senior / Manager]
        R --> C[Chef - Commis / Sous / Head / Executive]
    end

    subgraph Menu & Culinary Domain
        M --> MS[MenuSection - Soups / Appetizers / Mains]
        MS --> D[Dish - Culinary Definition & Price]
        D --> REC[Recipe - Cooking Instructions]
        REC --> RI[RecipeIngredient - Usage Quantity]
        RI --> ING[Ingredient - Solid / Liquid Stock in Grams/ML]
    end

    subgraph Customer Visit & Multi-Round Ordering
        VM --> CV[CustomerVisit - State Machine]
        CV --> ORD1[Order Round 1: Starters]
        CV --> ORD2[Order Round 2: Mains]
        ORD1 --> OI[OrderItem]
        OI --> DEC[DishCustomizationDecorator - Extra Topping / Spice / Protein]
    end

    subgraph Kitchen Display System KDS
        EP -->|OrderPlacedEvent| HK[KitchenDisplayListener - Hot Kitchen]
        EP -->|OrderPlacedEvent| CS[KitchenDisplayListener - Cold Station]
        EP -->|OrderPlacedEvent| NOTIF[CustomerNotificationListener - Table Alerts]
        EP -->|All Events| AUDIT[KitchenAuditLogListener - Event Trail]
    end

    subgraph Settlement & Split Billing
        CV --> BILL[Bill - Itemized Breakdown]
        BILL --> STRAT[BillingStrategy - Tax & Service Calculations]
        BILL --> PAY1[Payment 1: UPI Partial]
        BILL --> PAY2[Payment 2: Card Remainder]
    end
```

---

## 3. Package & Directory Structure

```
src/main/java/com/restaurant
├── Restaurant.java                           # Facade Aggregate Root
├── builder/
│   ├── MenuBuilder.java                      # Fluent menu assembly
│   ├── RecipeBuilder.java                    # Fluent recipe assembly
│   └── RestaurantBuilder.java                # Fluent restaurant assembly
├── enums/
│   ├── BillStatus.java                       # UNPAID, PARTIALLY_PAID, PAID, REFUNDED
│   ├── ChefLevel.java                        # COMMIS_CHEF, SOUS_CHEF, HEAD_CHEF, EXECUTIVE_CHEF
│   ├── CuisineType.java                      # ITALIAN, FRENCH, CHINESE, INDIAN, etc.
│   ├── DietaryType.java                      # VEG, NON_VEG, VEGAN
│   ├── IngredientType.java                   # SOLID, LIQUID
│   ├── KitchenStation.java                   # HOT_KITCHEN, COLD_STATION, BAR, DESSERT_STATION
│   ├── MeasurementUnit.java                  # GRAMS (solids), MILLILITERS (liquids)
│   ├── OrderStatus.java                      # PLACED, PREPARING, READY, SERVED, CANCELLED
│   ├── PaymentMethod.java                    # CASH, CREDIT_CARD, DEBIT_CARD, UPI
│   ├── VisitStatus.java                      # ACTIVE, BILLED, COMPLETED, CANCELLED
│   └── WaiterLevel.java                      # JUNIOR, SENIOR, MANAGER
├── exception/
│   ├── InsufficientInventoryException.java   # Raised on ingredient stock depletion
│   ├── InvalidPaymentException.java          # Raised on overpayment or invalid transaction
│   ├── InvalidVisitStateException.java       # Raised on out-of-order visit state transitions
│   └── TableAlreadyOccupiedException.java    # Raised on double-booking tables
├── model/
│   ├── Address.java                          # Immutable postal address Value Object
│   ├── Chef.java                             # Chef entity with ChefLevel and station
│   ├── Customer.java                         # Dining customer entity
│   ├── CustomerVisit.java                    # Dining visit aggregate managing multi-round orders
│   ├── Dish.java                             # Master dish with price, dietary type, and recipe
│   ├── Employee.java                         # Abstract base employee entity
│   ├── Ingredient.java                       # Raw inventory stock item (solid in g, liquid in ml)
│   ├── Menu.java                             # Restaurant catalog root
│   ├── MenuSection.java                      # Menu section holding list of dishes
│   ├── Order.java                            # Order round aggregate
│   ├── OrderItem.java                        # Line item with quantity and customizations
│   ├── Recipe.java                           # Cooking instructions and required ingredients
│   ├── RecipeIngredient.java                 # Contextual quantity and unit for a recipe
│   ├── Table.java                            # Dining table entity with capacity and status
│   └── Waiter.java                           # Waiter entity with WaiterLevel and table assignments
├── customization/
│   ├── BaseDishItem.java                     # Concrete component wrapping base Dish
│   ├── DishCustomizationDecorator.java       # Abstract decorator for orderable items
│   ├── ExtraToppingDecorator.java            # Decorator adding toppings (e.g. Parmesan, Olives)
│   ├── OrderableItem.java                    # Component interface for pricing and dietary type
│   ├── ProteinDecorator.java                 # Decorator adding protein and switching dietary type
│   └── SpiceLevelDecorator.java              # Decorator modifying dish spice intensity
├── billing/
│   ├── Bill.java                             # Itemized bill with split-payment ledger
│   ├── Payment.java                          # Immutable payment transaction record
│   └── strategy/
│       ├── BillingStrategy.java              # Financial calculation strategy interface
│       ├── DiscountedBillingStrategy.java    # Promotional / loyalty discount calculations
│       └── StandardBillingStrategy.java      # Standard tax + service charge calculations
├── event/
│   ├── OrderEvent.java                       # Abstract base order event
│   ├── OrderPlacedEvent.java                 # Event dispatched when an order is placed
│   ├── OrderStatusChangedEvent.java          # Event dispatched on order state advancement
│   ├── OrderEventListener.java               # Observer listener interface
│   ├── OrderEventPublisher.java              # Synchronous event bus dispatcher
│   └── listener/
│       ├── CustomerNotificationListener.java # Table alert listener
│       ├── KitchenAuditLogListener.java      # Comprehensive audit trail listener
│       └── KitchenDisplayListener.java       # KDS station screen listener
└── service/
    ├── KitchenService.java                   # Order preparation, stock deduction, and status routing
    └── VisitManager.java                     # Thread-safe dining visit manager and table tracker
```

---

## 4. Key Design Patterns Implemented

### 1. Strategy Pattern (Pluggable Billing Engine)
- Decouples financial algorithms (tax rates, service charge, promotional discounts) from `CustomerVisit`.
- `StandardBillingStrategy` computes standard tax and service charges.
- `DiscountedBillingStrategy` applies percentage discounts before computing statutory levies.

### 2. Observer Pattern (Kitchen Display System Event Bus)
- Decouples order placement from peripheral kitchen display systems.
- When `VisitManager.placeOrder()` is called, `OrderPlacedEvent` is published to `OrderEventPublisher`.
- `KitchenDisplayListener` routes the order to the appropriate station display (`HOT_KITCHEN`, `COLD_STATION`, `BAR`).
- `CustomerNotificationListener` and `KitchenAuditLogListener` independently receive updates without tight coupling.

### 3. Decorator Pattern (Customizable Dishes)
- Solves combinatorial class explosion for dish customizations ($4 \text{ proteins} \times 4 \text{ toppings} \times 3 \text{ spice levels} = 48\text{ variants}$).
- `OrderableItem` is the component interface implemented by `BaseDishItem`.
- `DishCustomizationDecorator` wraps an `OrderableItem`, allowing dynamic wrapping with `ExtraToppingDecorator`, `SpiceLevelDecorator`, and `ProteinDecorator` (which switches `DietaryType` dynamically).

### 4. State Pattern (Customer Visit Lifecycle)
- `CustomerVisit` acts as the state boundary for a dining party's lifecycle:
  $$\text{ACTIVE} \xrightarrow{\text{generateBill()}} \text{BILLED} \xrightarrow{\text{makePayment() (all settled)}} \text{COMPLETED}$$
- Guards against out-of-order operations:
  - Cannot place orders once the visit is `BILLED`.
  - Cannot complete visit while remaining bill balance $> 0$.
  - Automatically frees table and unassigns waiter upon completion.

### 5. Builder Pattern (Fluent Aggregate Assembly)
- `RestaurantBuilder`, `MenuBuilder`, and `RecipeBuilder` provide clean, fluent construction of multi-nested aggregates with invariant validations.

---

## 5. Verification & Test Suite

The test suite runs **40 automated tests** using JUnit 5, organized into cohesive, domain-aligned test packages:

```bash
mvn clean test
```

```
[INFO] Results:
[INFO] 
[INFO] Tests run: 40, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Test Coverage Highlights
- **`DomainModelTest`**: Solid/liquid measurement in grams/ml, stock restock/deductions, table occupancy, address formatting, customer identity.
- **`StaffAndLevelTest`**: Waiter levels (`Junior, Senior, Manager`), table assignments, Chef levels (`Commis, Sous, Head, Executive`), kitchen station assignments.
- **`DishCustomizationTest`**: Multi-layer decorator stacking (Base + Topping + Spice + Protein), price calculations, dietary type switching.
- **`BillingAndSplitPaymentTest`**: Standard and discounted billing strategies, multi-method split payments (Cash + UPI + Card), overpayment rejection.
- **`OrderEventBusTest`**: Publisher-subscriber routing, station display queues, customer notifications, and audit logging.
- **`VisitManagerTest`**: Multi-round order placement, table double-booking prevention, state transitions, and visit completion.
- **`KitchenServiceTest`**: Recipe stock deductions, insufficient inventory rejection, order advancement (`PLACED` $\to$ `PREPARING` $\to$ `READY` $\to$ `SERVED`), cancellation rules.
- **`RestaurantBuilderTest`**: Fluent assembly of complex nested aggregates with invariant validation.
- **`ConcurrentVisitAndStockStressTest`**: Multi-threaded concurrency stress tests (`CountDownLatch`, `ExecutorService`) proving race-condition safety on table bookings and atomic stock deductions.
- **`RestaurantIntegrationTest`**: End-to-end simulation from customer seating, multi-round ordering, kitchen prep, split billing, to visit completion.
