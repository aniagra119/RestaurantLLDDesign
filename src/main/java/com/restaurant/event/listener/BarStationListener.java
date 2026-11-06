package com.restaurant.event.listener;

import com.restaurant.enums.KitchenStation;
import com.restaurant.event.KitchenEvent;
import com.restaurant.event.KitchenEventListener;
import com.restaurant.event.OrderPlacedEvent;
import com.restaurant.model.Dish;
import com.restaurant.model.OrderItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BarStationListener implements KitchenEventListener {
    private final List<String> activeDrinkTickets = new ArrayList<>();

    @Override
    public synchronized void onEvent(KitchenEvent event) {
        if (event instanceof OrderPlacedEvent orderPlaced) {
            for (OrderItem item : orderPlaced.getOrder().getItems()) {
                Dish rootDish = item.getPreparedItem().getRootDish();
                if (rootDish != null && rootDish.getTargetStation() == KitchenStation.BAR_DISPENSE) {
                    activeDrinkTickets.add("[" + orderPlaced.getTableNumber() + "] " + item.getQuantity() + "x " + item.getPreparedItem().getName());
                }
            }
        }
    }

    public synchronized List<String> getActiveDrinkTickets() {
        return Collections.unmodifiableList(activeDrinkTickets);
    }

    public synchronized void clear() {
        activeDrinkTickets.clear();
    }
}

