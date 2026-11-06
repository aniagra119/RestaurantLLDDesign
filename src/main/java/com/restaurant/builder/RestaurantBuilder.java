package com.restaurant.builder;

import com.restaurant.Restaurant;
import com.restaurant.event.KitchenEventPublisher;
import com.restaurant.model.Address;
import com.restaurant.model.Chef;
import com.restaurant.model.Cuisine;
import com.restaurant.model.Menu;
import com.restaurant.model.Waiter;
import com.restaurant.strategy.billing.BillingStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RestaurantBuilder {
    private String name;
    private Address address;
    private Menu menu;
    private final Set<Cuisine> cuisines = new HashSet<>();
    private final List<Waiter> waiters = new ArrayList<>();
    private final List<Chef> chefs = new ArrayList<>();
    private KitchenEventPublisher eventPublisher;
    private BillingStrategy billingStrategy;

    public static RestaurantBuilder create() {
        return new RestaurantBuilder();
    }

    public RestaurantBuilder name(String name) {
        this.name = name;
        return this;
    }

    public RestaurantBuilder address(String street, String city, String state, String postalCode, String country) {
        this.address = new Address(street, city, state, postalCode, country);
        return this;
    }

    public RestaurantBuilder address(Address address) {
        this.address = address;
        return this;
    }

    public RestaurantBuilder menu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public RestaurantBuilder addCuisine(Cuisine cuisine) {
        this.cuisines.add(cuisine);
        return this;
    }

    public RestaurantBuilder addWaiter(Waiter waiter) {
        this.waiters.add(waiter);
        return this;
    }

    public RestaurantBuilder addChef(Chef chef) {
        this.chefs.add(chef);
        return this;
    }

    public RestaurantBuilder eventPublisher(KitchenEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        return this;
    }

    public RestaurantBuilder billingStrategy(BillingStrategy billingStrategy) {
        this.billingStrategy = billingStrategy;
        return this;
    }

    public Restaurant build() {
        Objects.requireNonNull(name, "Restaurant name cannot be null");
        Objects.requireNonNull(address, "Address cannot be null");
        Objects.requireNonNull(menu, "Menu cannot be null");

        Restaurant restaurant = new Restaurant(name, address, menu, eventPublisher);
        for (Cuisine c : cuisines) {
            restaurant.addCuisine(c);
        }
        for (Waiter w : waiters) {
            restaurant.addWaiter(w);
        }
        for (Chef ch : chefs) {
            restaurant.addChef(ch);
        }
        if (billingStrategy != null) {
            restaurant.setDefaultBillingStrategy(billingStrategy);
        }
        return restaurant;
    }
}

