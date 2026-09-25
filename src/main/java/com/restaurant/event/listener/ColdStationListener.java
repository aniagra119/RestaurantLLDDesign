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

public class ColdStationListener implements KitchenEventListener {
    private final List<String> activeColdTickets = new ArrayList<>();

    @Override
    public synchronized void onEvent(KitchenEvent event) {
        if (event instanceof OrderPlacedEvent orderPlaced) {
            for (OrderItem item : orderPlaced.getOrder().getItems()) {
                Dish rootDish = item.getPreparedItem().getRootDish();
                if (rootDish != null && rootDish.getTargetStation() == KitchenStation.COLD_COUNTER) {
                    activeColdTickets.add("[" + orderPlaced.getTableNumber() + "] " + item.getQuantity() + "x " + item.getPreparedItem().getName());
                }
            }
        }
    }

    public synchronized List<String> getActiveColdTickets() {
        return Collections.unmodifiableList(activeColdTickets);
    }

    public synchronized void clear() {
        activeColdTickets.clear();
    }
}

