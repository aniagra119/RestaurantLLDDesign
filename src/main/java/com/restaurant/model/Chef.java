package com.restaurant.model;

import com.restaurant.enums.ChefLevel;
import com.restaurant.enums.KitchenStation;

import java.util.Objects;

public class Chef extends Employee {
    private final ChefLevel level;
    private final KitchenStation primaryStation;

    public Chef(String id, String name, String email, String phoneNumber, ChefLevel level) {
        this(id, name, email, phoneNumber, level, KitchenStation.HOT_KITCHEN);
    }

    public Chef(String id, String name, String email, String phoneNumber, ChefLevel level, KitchenStation primaryStation) {
        super(id, name, email, phoneNumber);
        this.level = Objects.requireNonNull(level, "Chef level cannot be null");
        this.primaryStation = primaryStation != null ? primaryStation : KitchenStation.HOT_KITCHEN;
    }

    public ChefLevel getLevel() {
        return level;
    }

    public KitchenStation getPrimaryStation() {
        return primaryStation;
    }
}
