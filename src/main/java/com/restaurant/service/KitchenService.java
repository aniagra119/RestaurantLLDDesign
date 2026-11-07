package com.restaurant.service;

import com.restaurant.customization.BaseDishItem;
import com.restaurant.customization.DishCustomizationDecorator;
import com.restaurant.customization.OrderableItem;
import com.restaurant.enums.OrderStatus;
import com.restaurant.event.OrderEventPublisher;
import com.restaurant.event.OrderStatusChangedEvent;
import com.restaurant.model.Chef;
import com.restaurant.model.Dish;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.Recipe;
import com.restaurant.model.RecipeIngredient;

import java.util.Objects;
import java.util.UUID;

public class KitchenService {
    private final OrderEventPublisher eventPublisher;

    public KitchenService(OrderEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "Event publisher cannot be null");
    }

    public synchronized void startPreparingOrder(Order order, Chef chef) {
        Objects.requireNonNull(order, "Order cannot be null");
        Objects.requireNonNull(chef, "Chef cannot be null");

        // Deduct inventory stock for all dish recipes in the order
        for (OrderItem item : order.getItems()) {
            Dish dish = extractBaseDish(item.getItem());
            if (dish != null) {
                Recipe recipe = dish.getRecipe();
                for (RecipeIngredient ri : recipe.getIngredients()) {
                    ri.getIngredient().deductStock(ri.getAmount() * item.getQuantity());
                }
            }
        }

        // Accept order into chef's queue (checks capacity limits)
        chef.acceptOrder(order);

        OrderStatus oldStatus = order.getStatus();
        order.updateStatus(OrderStatus.PREPARING);
        eventPublisher.publishStatusChanged(new OrderStatusChangedEvent(
                UUID.randomUUID().toString(), order, oldStatus, OrderStatus.PREPARING));
    }

    public synchronized void markOrderReady(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        Chef chef = order.getAssignedChef();
        if (chef != null) {
            chef.completeOrder(order);
        }

        OrderStatus oldStatus = order.getStatus();
        order.updateStatus(OrderStatus.READY);
        eventPublisher.publishStatusChanged(new OrderStatusChangedEvent(
                UUID.randomUUID().toString(), order, oldStatus, OrderStatus.READY));
    }

    public synchronized void serveOrder(Order order) {
        Objects.requireNonNull(order, "Order cannot be null");
        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Order must be READY before being served. Current: " + order.getStatus());
        }
        OrderStatus oldStatus = order.getStatus();
        order.updateStatus(OrderStatus.SERVED);
        eventPublisher.publishStatusChanged(new OrderStatusChangedEvent(
                UUID.randomUUID().toString(), order, oldStatus, OrderStatus.SERVED));
    }

    public synchronized void cancelOrder(Order order, String reason) {
        Objects.requireNonNull(order, "Order cannot be null");
        if (order.getStatus() == OrderStatus.SERVED) {
            throw new IllegalStateException("Cannot cancel an order that has already been SERVED");
        }
        Chef chef = order.getAssignedChef();
        if (chef != null) {
            chef.completeOrder(order);
        }

        OrderStatus oldStatus = order.getStatus();
        order.updateStatus(OrderStatus.CANCELLED);
        eventPublisher.publishStatusChanged(new OrderStatusChangedEvent(
                UUID.randomUUID().toString(), order, oldStatus, OrderStatus.CANCELLED));
    }

    private Dish extractBaseDish(OrderableItem item) {
        if (item instanceof BaseDishItem baseDish) {
            return baseDish.getDish();
        } else if (item instanceof DishCustomizationDecorator decorator) {
            return extractBaseDish(decorator.getWrappedItem());
        }
        return null;
    }
}
