package com.restaurant;

import com.restaurant.billing.Bill;
import com.restaurant.billing.strategy.BillingStrategy;
import com.restaurant.enums.CuisineType;
import com.restaurant.event.OrderEventPublisher;
import com.restaurant.model.Address;
import com.restaurant.model.Chef;
import com.restaurant.model.Customer;
import com.restaurant.model.CustomerVisit;
import com.restaurant.model.Menu;
import com.restaurant.model.Order;
import com.restaurant.model.Table;
import com.restaurant.model.Waiter;
import com.restaurant.service.KitchenService;
import com.restaurant.service.VisitManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Restaurant {
    private final String name;
    private final Address address;
    private final List<CuisineType> cuisines;
    private final Menu menu;
    private final List<Waiter> waiters;
    private final List<Chef> chefs;
    private final List<Table> tables;
    private final VisitManager visitManager;
    private final KitchenService kitchenService;
    private final OrderEventPublisher eventPublisher;

    public Restaurant(String name,
                      Address address,
                      List<CuisineType> cuisines,
                      Menu menu,
                      List<Waiter> waiters,
                      List<Chef> chefs,
                      List<Table> tables,
                      VisitManager visitManager,
                      KitchenService kitchenService,
                      OrderEventPublisher eventPublisher) {
        this.name = Objects.requireNonNull(name, "Restaurant name cannot be null");
        this.address = Objects.requireNonNull(address, "Address cannot be null");
        this.cuisines = new ArrayList<>(Objects.requireNonNull(cuisines, "Cuisines cannot be null"));
        this.menu = Objects.requireNonNull(menu, "Menu cannot be null");
        this.waiters = new ArrayList<>(Objects.requireNonNull(waiters, "Waiters cannot be null"));
        this.chefs = new ArrayList<>(Objects.requireNonNull(chefs, "Chefs cannot be null"));
        this.tables = new ArrayList<>(Objects.requireNonNull(tables, "Tables cannot be null"));
        this.visitManager = Objects.requireNonNull(visitManager, "Visit manager cannot be null");
        this.kitchenService = Objects.requireNonNull(kitchenService, "Kitchen service cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher cannot be null");
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public List<CuisineType> getCuisines() {
        return Collections.unmodifiableList(cuisines);
    }

    public Menu getMenu() {
        return menu;
    }

    public List<Waiter> getWaiters() {
        return Collections.unmodifiableList(waiters);
    }

    public List<Chef> getChefs() {
        return Collections.unmodifiableList(chefs);
    }

    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public VisitManager getVisitManager() {
        return visitManager;
    }

    public KitchenService getKitchenService() {
        return kitchenService;
    }

    public OrderEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    // High-level facade operations
    public CustomerVisit hostGuest(Customer customer, Table table, Waiter waiter) {
        return visitManager.startVisit(customer, table, waiter);
    }

    public void placeOrder(String visitId, Order order) {
        visitManager.placeOrder(visitId, order);
    }

    public Bill generateBill(String visitId, BillingStrategy strategy) {
        return visitManager.generateBill(visitId, strategy);
    }
}
