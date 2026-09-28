package com.restaurant.builder;

import com.restaurant.Restaurant;
import com.restaurant.enums.CuisineType;
import com.restaurant.event.OrderEventPublisher;
import com.restaurant.model.Address;
import com.restaurant.model.Chef;
import com.restaurant.model.Menu;
import com.restaurant.model.Table;
import com.restaurant.model.Waiter;
import com.restaurant.service.KitchenService;
import com.restaurant.service.VisitManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantBuilder {
    private String name;
    private Address address;
    private final List<CuisineType> cuisines = new ArrayList<>();
    private Menu menu;
    private final List<Waiter> waiters = new ArrayList<>();
    private final List<Chef> chefs = new ArrayList<>();
    private final List<Table> tables = new ArrayList<>();
    private OrderEventPublisher eventPublisher;

    public RestaurantBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public RestaurantBuilder setAddress(Address address) {
        this.address = address;
        return this;
    }

    public RestaurantBuilder addCuisine(CuisineType cuisine) {
        this.cuisines.add(cuisine);
        return this;
    }

    public RestaurantBuilder setMenu(Menu menu) {
        this.menu = menu;
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

    public RestaurantBuilder addTable(Table table) {
        this.tables.add(table);
        return this;
    }

    public RestaurantBuilder setEventPublisher(OrderEventPublisher publisher) {
        this.eventPublisher = publisher;
        return this;
    }

    public Restaurant build() {
        Objects.requireNonNull(name, "Restaurant name cannot be null");
        Objects.requireNonNull(address, "Address cannot be null");
        Objects.requireNonNull(menu, "Menu cannot be null");

        OrderEventPublisher publisher = (eventPublisher != null) ? eventPublisher : new OrderEventPublisher();
        VisitManager visitManager = new VisitManager(publisher);
        KitchenService kitchenService = new KitchenService(publisher);

        Restaurant restaurant = new Restaurant(name, address, cuisines, menu, waiters, chefs, tables, visitManager, kitchenService, publisher);
        return restaurant;
    }
}
