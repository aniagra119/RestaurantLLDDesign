# Architectural Decisions, Interview Traps & Socratic Notes

## 1. Core Architectural Dilemmas & Interview Trade-offs

### Dilemma 1: Why `Dish` Should Not Have a Price Field
- **The Junior Approach**: Adding `BigDecimal price` directly to `Dish`.
- **Why this fails in Enterprise Systems**:
  - The same dish is sold at different prices across different channels (e.g. In-house dining vs Swiggy/Zomato delivery vs Airport branch vs Happy Hour discount menu).
  - Tying price to `Dish` violates the Single Responsibility Principle by mixing culinary specifications (recipes, calories, dietary tags) with commercial catalog marketing.
- **The Staff Resolution**:
  - `Dish`: Pure culinary master record (`name`, `description`, `recipe`, `dietaryType`).
  - `MenuItem`: The commercial catalog entry binding `Dish` to a specific `Menu` with `price` and `isAvailable`.

---

### Dilemma 2: Combinatorial Variant Explosion vs Decorator Pattern
- **The Junior Approach**: Creating distinct subclasses or database rows for every permutation (`ChickenArrabiataPenne`, `VegArrabiataPenne`, `ChickenAlfredoPenne`, `VegAlfredoPenne`, etc.).
  - With 4 proteins, 4 sauces, and 3 pasta shapes, this requires **48 separate variant entities**.
- **The Staff Resolution**:
  - Employ the **GoF Decorator Pattern**.
  - `BaseDishItem` wraps the base pasta.
  - `ProteinDecorator` decorates the base item, adds the protein price, and dynamically adjusts the dietary type to `NON_VEG`.
  - `SauceDecorator` adds the sauce price and simmering instructions.
  - Total entities: **$4 + 4 + 3 = 11$ options**, zero Cartesian product explosion.

---

### Dilemma 3: Multi-Round Ordering & Temporal Session Modeling
- **The Junior Trap**: Binding an `Order` 1:1 to a `Customer` or `Table`.
- **The Reality of Dining**:
  - Customers sit at a table for 1 to 2 hours.
  - They place orders in distinct rounds:
    - Round 1 (12:05 PM): Appetizers & Drinks.
    - Round 2 (12:35 PM): Mains.
    - Round 3 (01:15 PM): Desserts & Coffee.
  - Each round is an independent `Order` routed to the kitchen, but all rounds belong to a single **`DiningSession`**.
- **State Machine Invariant**:
  - Once the customer requests the bill (`generateBill()`), the session transitions to `BILLED`.
  - Calling `addOrder()` in `BILLED` or `SETTLED` state throws `IllegalStateException`.

---

### Dilemma 4: Split Payments
- A bill is rarely settled via a single payment in group dining.
- Modeling `Bill (1) *-- (0..*) Payment` enables:
  - Partial payments via different methods (e.g. ₹1,000 via UPI + ₹610 via Credit Card).
  - Transitioning from `UNPAID` $\to$ `PARTIALLY_PAID` $\to$ `PAID`.
  - Strict guard against overpayments (`payment.amount > bill.getRemainingAmount()`).

---

## 2. Common Interview Traps & Red Flags

| Trap | Why Interviewers Fail Candidates | How We Solved It |
| :--- | :--- | :--- |
| **Using `double` / `float` for Money** | Binary floating-point arithmetic causes cumulative precision drift (`0.1 + 0.2 != 0.3`). | All monetary values use `BigDecimal` with explicit scale (`2, RoundingMode.HALF_UP`). |
| **Direct Waiter-to-Chef Coupling** | Hardcoding `waiter.giveToChef(chef)` couples front-of-house to individual kitchen personnel. | Decoupled via `KitchenService` and `KitchenEventPublisher` (Observer pattern). |
| **Primitive Obsession in Units** | Using `String unit` ("g", "ml", "spoon") leads to typos and impossible conversions. | `Unit` enum with dimensional categorization (`MASS`, `VOLUME`, `COUNT`) and base conversion factors. |
| **Mixing Reservation with Dining** | Making `Reservation` inherit or encapsulate `DiningSession`. A reservation is in the future; a session is in the present. | `Reservation.checkIn()` explicitly spawns a new active `DiningSession` on arrival. |
