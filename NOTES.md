# Architectural Decisions, Interview Traps & Socratic Notes

---

## 1. Core Architectural Dilemmas & Staff Trade-offs

### Dilemma 1: Fulfilling Exact Domain Contracts vs Enterprise Over-Engineering
- **The Pitfall**: In an attempt to show enterprise prowess, candidates often abstract away fundamental problem constraints. For example, replacing explicit `WaiterLevel` (`Junior, Senior, Manager`) and `ChefLevel` (`Commis, Sous, Head, Executive`) with generic RBAC `Role` and `Permission` strings, or removing `price` from `Dish` into decoupled commercial menus.
- **The Staff Resolution**:
  - Always satisfy the explicit domain contract first. `Waiter` must expose `WaiterLevel`, `Chef` must expose `ChefLevel`, and `Dish` must have a primary `price`.
  - Design patterns (Strategy, Observer, Decorator, Builder) should be applied to enhance and decouple operations, not obscure the domain model.

---

### Dilemma 2: Combinatorial Variant Explosion vs Decorator Pattern
- **The Junior Approach**: Creating distinct subclasses or database rows for every permutation (`ChickenArrabiataPenne`, `VegArrabiataPenne`, `ChickenAlfredoPenne`, `VegAlfredoPenne`, etc.).
  - With 4 proteins, 4 sauces, and 3 spice levels, this requires **48 separate variant entities**.
- **The Staff Resolution**:
  - Employ the **GoF Decorator Pattern**.
  - `BaseDishItem` wraps the base dish.
  - `ExtraToppingDecorator` decorates the base item and adds the topping price.
  - `SpiceLevelDecorator` adjusts the spice intensity.
  - `ProteinDecorator` adds the protein price and dynamically updates the dietary classification to `NON_VEG`.
  - Total entities: **$4 + 4 + 3 = 11$ options**, zero Cartesian product explosion.

---

### Dilemma 3: Multi-Round Ordering & Temporal Visit Modeling
- **The Junior Trap**: Binding an `Order` 1:1 to a `Customer` or `Table`.
- **The Reality of Dining**:
  - Customers sit at a table for 1 to 2 hours.
  - They place orders in distinct rounds:
    - Round 1 (12:05 PM): Appetizers & Drinks.
    - Round 2 (12:35 PM): Mains.
    - Round 3 (01:15 PM): Desserts & Coffee.
  - Each round is an independent `Order` routed to the kitchen, but all rounds belong to a single **`CustomerVisit`**.
- **State Machine Invariant**:
  - Once the customer requests the bill (`generateBill()`), the visit transitions to `BILLED`.
  - Calling `placeOrder()` in `BILLED` or `COMPLETED` state throws `InvalidVisitStateException`.

---

### Dilemma 4: Split Payments
- A bill is rarely settled via a single payment in group dining.
- Modeling `Bill (1) *-- (0..*) Payment` enables:
  - Partial payments via different methods (e.g. ₹1,000 via UPI + ₹840 via Credit Card).
  - Transitioning from `UNPAID` $\to$ `PARTIALLY_PAID` $\to$ `PAID`.
  - Strict guard against overpayments (`payment.amount > bill.getRemainingAmount()`).

---

### Dilemma 5: Physical Ingredient Measurements (Solids vs Liquids)
- **The Junior Trap**: Using `String unit` ("g", "ml", "spoon") leading to runtime bugs and arbitrary unit conversions.
- **The Staff Resolution**:
  - Explicit categorization: `IngredientType.SOLID` vs `IngredientType.LIQUID`.
  - Strict measurement unit invariants:
    - Solids are measured strictly in `MeasurementUnit.GRAMS`.
    - Liquids / semi-liquids are measured strictly in `MeasurementUnit.MILLILITERS`.
  - Recipes validate unit compatibility at instantiation, preventing dimensional errors.

---

## 2. Common Interview Traps & Red Flags

| Trap | Why Interviewers Fail Candidates | How We Solved It |
| :--- | :--- | :--- |
| **Missing Problem Requirements** | Abstracting away explicitly named fields like `WaiterLevel` or `ChefLevel` into generic models. | Direct domain modeling with strongly-typed enums and clean OOP encapsulation. |
| **Direct Waiter-to-Chef Coupling** | Hardcoding `waiter.giveToChef(chef)` couples front-of-house to individual kitchen personnel. | Decoupled via `OrderEventPublisher` and `KitchenDisplayListener` (Observer pattern). |
| **Primitive Obsession in Units** | Using raw strings for measurements without validation. | `MeasurementUnit` enum with strict solid (`GRAMS`) vs liquid (`MILLILITERS`) enforcement. |
| **Single-Order Billing Assumption** | Generating a bill immediately per order instead of aggregating across multi-round visit orders. | `CustomerVisit` acts as the aggregate boundary accumulating multi-round orders before billing. |
