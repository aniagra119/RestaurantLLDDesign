package com.restaurant;

import com.restaurant.event.KitchenEventPublisher;
import com.restaurant.event.OrderPlacedEvent;
import com.restaurant.model.Address;
import com.restaurant.model.Chef;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Customer;
import com.restaurant.model.DiningSession;
import com.restaurant.model.Menu;
import com.restaurant.model.Order;
import com.restaurant.model.Waiter;
import com.restaurant.service.DiningSessionManager;
import com.restaurant.service.KitchenService;
import com.restaurant.strategy.billing.BillingStrategy;
import com.restaurant.strategy.billing.StandardBillingStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Restaurant {
    private final String name;
    private final Address address;
    private final Set<Cuisine> cuisines;
    private Menu menu;
    private final List<Waiter> waiters;
    private final List<Chef> chefs;
    private final DiningSessionManager sessionManager;
    private final KitchenService kitchenService;
    private final KitchenEventPublisher eventPublisher;
    private BillingStrategy defaultBillingStrategy;

    public Restaurant(String name, Address address, Menu menu) {
        this(name, address, menu, new KitchenEventPublisher());
    }

    public Restaurant(String name, Address address, Menu menu, KitchenEventPublisher eventPublisher) {
        this.name = Objects.requireNonNull(name, "Restaurant name cannot be null");
        this.address = Objects.requireNonNull(address, "Address cannot be null");
        this.menu = Objects.requireNonNull(menu, "Menu cannot be null");
        this.eventPublisher = eventPublisher != null ? eventPublisher : new KitchenEventPublisher();
        this.cuisines = new HashSet<>();
        this.waiters = new ArrayList<>();
        this.chefs = new ArrayList<>();
        this.sessionManager = new DiningSessionManager();
        this.kitchenService = new KitchenService(this.eventPublisher);
        this.defaultBillingStrategy = StandardBillingStrategy.withDefaultRates();
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = Objects.requireNonNull(menu, "Menu cannot be null");
    }

    public void addCuisine(Cuisine cuisine) {
        this.cuisines.add(Objects.requireNonNull(cuisine, "Cuisine cannot be null"));
    }

    public Set<Cuisine> getCuisines() {
        return Collections.unmodifiableSet(cuisines);
    }

    public void addWaiter(Waiter waiter) {
        this.waiters.add(Objects.requireNonNull(waiter, "Waiter cannot be null"));
    }

    public List<Waiter> getWaiters() {
        return Collections.unmodifiableList(waiters);
    }

    public void addChef(Chef chef) {
        this.chefs.add(Objects.requireNonNull(chef, "Chef cannot be null"));
    }

    public List<Chef> getChefs() {
        return Collections.unmodifiableList(chefs);
    }

    public DiningSessionManager getSessionManager() {
        return sessionManager;
    }

    public KitchenService getKitchenService() {
        return kitchenService;
    }

    public KitchenEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public void setDefaultBillingStrategy(BillingStrategy billingStrategy) {
        this.defaultBillingStrategy = Objects.requireNonNull(billingStrategy, "BillingStrategy cannot be null");
    }

    public BillingStrategy getDefaultBillingStrategy() {
        return defaultBillingStrategy;
    }

    public DiningSession startDiningSession(String sessionId, String tableNumber, Customer customer, Waiter waiter) {
        return sessionManager.openSession(sessionId, tableNumber, customer, waiter);
    }

    public void placeOrder(DiningSession session, Order order) {
        Objects.requireNonNull(session, "Session cannot be null");
        Objects.requireNonNull(order, "Order cannot be null");
        session.addOrder(order);
        eventPublisher.publish(new OrderPlacedEvent(session.getSessionId(), session.getTableNumber(), order));
    }

    public void cookOrder(Order order, Chef chef) {
        kitchenService.prepareOrder(order, chef);
    }

    public void cancelOrder(Order order, String reason) {
        kitchenService.cancelOrder(order, reason);
    }
}
