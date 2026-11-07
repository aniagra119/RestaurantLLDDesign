# Enterprise Restaurant Management System — Master Architecture & Engineering Guide

A comprehensive, production-grade reference and deep-dive revision guide for designing, implementing, and scaling enterprise restaurant management, Point-of-Sale (POS), and Kitchen Display Systems (KDS).

---

## 1. System Domain Decomposition & Responsibility Boundaries

A robust Low-Level Design cleanly separates physical dining infrastructure, guest session lifecycles, menu catalogs, culinary recipes, raw ingredient inventory, kitchen station displays, and financial settlement engines.

```
                      ┌─────────────────────────────────────────┐
                      │           Restaurant (Facade)           │
                      └────────────────────┬────────────────────┘
                                           │
         ┌─────────────────────────────────┴─────────────────────────────────┐
         ▼                                                                   ▼
┌─────────────────────────────────┐                         ┌─────────────────────────────────┐
│     Dining & Visit Manager      │                         │     Catalog & Culinary Core     │
│  - Table (Capacity / Status)    │                         │  - Menu (Catalog Root)          │
│  - CustomerVisit (State Machine)│                         │  - MenuSection (Logical Course) │
│  - Customer (Guest Entity)      │                         │  - Dish (Culinary Spec & Price) │
│  - Waiter (Staff Level & Tables)│                         │  - Recipe (Prep Instructions)   │
└────────────────┬────────────────┘                         └────────────────┬────────────────┘
                 │ dispatches multi-round orders                             │ provides base items
                 ▼                                                           ▼
┌─────────────────────────────────┐                         ┌─────────────────────────────────┐
│    Order & Customization Engine │                         │   Inventory & Physical Units    │
│  - Order (Round Aggregate)      │                         │  - Ingredient (Atomic Stock)    │
│  - OrderItem (Line Customization│                         │  - IngredientType (Solid/Liquid)│
│  - OrderableItem (Component)    │                         │  - MeasurementUnit (Grams / ML) │
│  - DishCustomizationDecorator   │                         │  - RecipeIngredient (Usage)     │
└────────────────┬────────────────┘                         └────────────────┬────────────────┘
                 │ notifies on status change                                 │ atomic stock deduction
                 ▼                                                           ▼
┌─────────────────────────────────┐                         ┌─────────────────────────────────┐
│    Kitchen Display System (KDS) │                         │   Billing & Split Settlement    │
│  - OrderEventPublisher (Bus)    │                         │  - Bill (Itemized Ledger)       │
│  - KitchenDisplayListener       │                         │  - BillingStrategy (Strategy)   │
│  - CustomerNotificationListener │                         │  - Payment (Immutable Txn)      │
│  - KitchenAuditLogListener      │                         │  - PaymentMethod (UPI/Card/Cash)│
└─────────────────────────────────┘                         └─────────────────────────────────┘
```

### Component Responsibility Matrix

| Layer / Component | Type | Core Responsibility | Anti-Patterns to Avoid |
| :--- | :--- | :--- | :--- |
| **`Restaurant`** | Facade | High-level system lifecycle, catalog registry, employee directory, and table orchestration. | **Never** calculate tax math or micromanage individual table locking directly. |
| **`Table`** | Domain Entity | Encapsulates physical bay seating, guest capacity, and atomic occupancy state (`isOccupied`). | **Never** hold order histories or process payments. |
| **`CustomerVisit`** | Aggregate / State Machine | Tracks a dining party across multi-round orders. Enforces dining lifecycle invariants (`ACTIVE` $\to$ `BILLED` $\to$ `COMPLETED`). | **Never** bypass bill generation to mark completion prematurely. |
| **`Waiter`** | Staff Entity | Manages service tier (`WaiterLevel`: `Junior, Senior, Manager`) and holds assigned table collections. | **Never** cook recipes or deduct kitchen inventory. |
| **`Chef`** | Staff Entity | Encapsulates kitchen tier (`ChefLevel`: `Commis, Sous, Head, Executive`) and primary station ownership. | **Never** seat guests or generate guest billing checks. |
| **`Dish`** | Domain Entity | Master culinary definition holding base price, dietary classification (`VEG`, `NON_VEG`, `VEGAN`), target kitchen station, and recipe. | **Never** create subclass variants for dynamic toppings or spice levels. |
| **`Recipe`** | Domain Entity | Culinary preparation instructions, prep time, and required recipe ingredient usages. | **Never** mutate inventory stock directly (delegate to `Ingredient`). |
| **`Ingredient`** | Inventory Entity | Tracks atomic physical stock. Enforces strict dimensional compatibility: **Solids in GRAMS**, **Liquids in MILLILITERS**. | **Never** allow stock to drop below zero (fail-fast with `InsufficientInventoryException`). |
| **`BillingStrategy`** | Strategy Port | Calculates itemized subtotals, statutory taxes, service fees, and promotional discounts. | **Never** process credit cards or mutate visit state. |
| **`Bill`** | Aggregate Entity | Itemized financial ledger supporting partial settlements across heterogeneous payment instruments. | **Never** accept payments exceeding the remaining balance. |
| **`OrderEventPublisher`** | Observer Bus | Publishes order placement and state transition events to decoupled station displays and audit logs. | **Never** tightly couple the order workflow to specific hardware printers or KDS terminals. |

---

## 2. Kitchen Display System (KDS) & Station Routing Pipeline

In high-volume commercial kitchens, tickets are split and routed to specialized preparation lines:

```
[ Dining Table 4 ]  ──►  Order Placed  ──►  [ OrderEventPublisher ]
                                                    │
         ┌──────────────────────────────────────────┼──────────────────────────────────────────┐
         ▼                                          ▼                                          ▼
┌───────────────────────────────┐  ┌───────────────────────────────┐  ┌───────────────────────────────┐
│   Hot Kitchen Station KDS    │  │     Cold Station KDS          │  │       Bar Dispense KDS        │
│  - Tagliatelle Pasta          │  │  - Burrata Caprese Salad      │  │  - Negroni Cocktail           │
│  - Woodfired Pizza            │  │  - Tiramisu Dessert           │  │  - Sparkling Mineral Water    │
│  Station: HOT_KITCHEN         │  │  Station: COLD_STATION        │  │  Station: BAR                 │
└───────────────────────────────┘  └───────────────────────────────┘  └───────────────────────────────┘
```

### Order Lifecycle State Machine

```
   ┌───────────────┐
   │    PLACED     │◄── Guest submits round order via Waiter
   └───────┬───────┘
           │ Chef claims ticket on KDS screen; raw inventory atomically deducted
           ▼
   ┌───────────────┐
   │   PREPARING   │
   └───────┬───────┘
           │ Kitchen line finishes cooking; item placed on pass counter
           ▼
   ┌───────────────┐
   │     READY     │
   └───────┬───────┘
           │ Waiter picks up dish from pass and serves table
           ▼
   ┌───────────────┐
   │    SERVED     │ (Terminal order state; cannot be cancelled)
   └───────────────┘
```

---

## 3. Concurrency Invariants & Table Double-Booking Prevention

### 1. Atomic Table Reservation Interlock
In a crowded restaurant, multiple hosts or automated reservation systems may attempt to seat guests simultaneously at the same table.
```java
public synchronized CustomerVisit startVisit(Customer customer, Table table, Waiter waiter) {
    if (table.isOccupied() || tableToVisitMap.containsKey(table.getTableNumber())) {
        throw new TableAlreadyOccupiedException("Table " + table.getTableNumber() + " is already occupied");
    }
    // Atomic state transition
    table.markOccupied();
    waiter.assignTable(table);
    ...
}
```
- **Stress-Tested Guarantee**: Under 10 concurrent threads contending for a single VIP table, exactly 1 thread succeeds; all 9 others receive `TableAlreadyOccupiedException` with zero table state corruption.

### 2. Thread-Safe Inventory Stock Depletion
When two chefs simultaneously begin preparing 2 orders of Truffle Tagliatelle requiring flour and butter:
```java
public synchronized void deductStock(double amount) {
    if (currentStock < amount) {
        throw new InsufficientInventoryException(...);
    }
    this.currentStock -= amount;
}
```
- **Invariant**: Synchronized atomic stock deductions ensure inventory balances never drop below zero, eliminating phantom stock race conditions.

---

## 4. Financial Mathematics & Split Settlement Ledger

### The Multi-Payer Group Dining Invariant
In group dining, parties split bills across multiple methods (`CASH`, `UPI`, `CREDIT_CARD`, `DEBIT_CARD`).
- **Mathematical Invariant**:
  $$\text{Remaining Balance} = \max\left(0.0, \; \text{Total Payable} - \sum_{i=1}^{k} \text{Payment}_i\right)$$
- **State Transition Guard**:
  - `status = UNPAID` when $\sum \text{Payment} = 0$.
  - `status = PARTIALLY_PAID` when $0 < \sum \text{Payment} < \text{Total Payable}$.
  - `status = PAID` when $\text{Remaining Balance} \le 0.001$.
- **Overpayment Rejection**: Attempting to pay more than `getRemainingAmount()` immediately throws `InvalidPaymentException`.

---

## 5. Socratic Interview Trade-offs & Staff Rubrics

| Dimension | Junior Approach | Senior / Staff Resolution | Why Evaluators Care |
| :--- | :--- | :--- | :--- |
| **Dish Customization** | Creating 48 subclasses (`ChickenPasta`, `VegSpicyPasta`, etc.). | **Decorator Pattern** (`OrderableItem`, `BaseDishItem`, `DishCustomizationDecorator`). | Tests ability to handle Cartesian combinatorial explosions dynamically. |
| **Staff Modeling** | Hardcoding arbitrary permissions or deleting levels entirely. | Explicit **`WaiterLevel`** and **`ChefLevel`** enums mapped to station responsibilities. | Demonstrates requirement compliance and domain modeling fidelity. |
| **Multi-Round Dining** | Treating every order as a detached transaction. | **`CustomerVisit` aggregate root** accumulating multi-round orders before terminal billing. | Reflects real-world business domains instead of naive tutorial assumptions. |
| **Physical Measurements** | Free-text strings ("grams", "ml", "spoon"). | **`MeasurementUnit`** strictly enforcing solids in `GRAMS` and liquids in `MILLILITERS`. | Prevents dimensional incompatibility bugs in recipe preparation. |
| **KDS Coupling** | Direct invocation between waiter and kitchen staff. | **`OrderEventPublisher`** pub-sub event bus. | Decouples front-of-house from back-of-house operations. |
