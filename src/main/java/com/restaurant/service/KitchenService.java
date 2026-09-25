package com.restaurant.service;

import com.restaurant.enums.OrderStatus;
import com.restaurant.event.ItemPreparedEvent;
import com.restaurant.event.KitchenEventPublisher;
import com.restaurant.event.OrderCancelledEvent;
import com.restaurant.model.Chef;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.RecipeIngredient;

import java.util.Objects;

public class KitchenService {
    private final KitchenEventPublisher eventPublisher;

    public KitchenService() {
        this(new KitchenEventPublisher());
    }

    public KitchenService(KitchenEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "KitchenEventPublisher cannot be null");
    }

    public KitchenEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public synchronized void prepareOrder(Order order, Chef chef) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(chef, "Chef cannot be null");
        if (!chef.canPrepareFood()) {
            throw new IllegalStateException("Chef " + chef.getName() + " does not have PREPARE_FOOD permission");
        }

        order.updateStatus(OrderStatus.PREPARING);

        // Deduct inventory for all items in order
        for (OrderItem orderItem : order.getItems()) {
            int qty = orderItem.getQuantity();
            for (RecipeIngredient ri : orderItem.getPreparedItem().getIngredients()) {
                double totalBaseAmount = ri.getBaseAmount() * qty;
                ri.getIngredient().deductStock(totalBaseAmount);
            }
            eventPublisher.publish(new ItemPreparedEvent(order.getId(), orderItem.getId(), chef.getId()));
        }

        order.updateStatus(OrderStatus.READY);
    }

    public synchronized void markOrderServed(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Order must be READY before being SERVED. Current status: " + order.getStatus());
        }
        order.updateStatus(OrderStatus.SERVED);
    }

    public synchronized void cancelOrder(Order order, String reason) {
        Objects.requireNonNull(order, "Order cannot be null");
        if (order.getStatus() == OrderStatus.SERVED) {
            throw new IllegalStateException("Cannot cancel an order that has already been SERVED");
        }
        order.updateStatus(OrderStatus.CANCELLED);
        eventPublisher.publish(new OrderCancelledEvent(order.getId(), reason));
    }
}
