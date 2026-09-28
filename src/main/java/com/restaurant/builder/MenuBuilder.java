package com.restaurant.builder;

import com.restaurant.model.Dish;
import com.restaurant.model.Menu;
import com.restaurant.model.MenuSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuBuilder {
    private String menuId;
    private String name;
    private final Map<String, List<Dish>> sectionMap = new HashMap<>();

    public MenuBuilder setMenuId(String menuId) {
        this.menuId = menuId;
        return this;
    }

    public MenuBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MenuBuilder addDishToSection(String sectionName, Dish dish) {
        sectionMap.computeIfAbsent(sectionName, k -> new ArrayList<>()).add(dish);
        return this;
    }

    public Menu build() {
        if (menuId == null) {
            menuId = "MENU-" + System.currentTimeMillis();
        }
        if (name == null) {
            name = "Main Dine-in Menu";
        }
        List<MenuSection> sections = new ArrayList<>();
        for (Map.Entry<String, List<Dish>> entry : sectionMap.entrySet()) {
            sections.add(new MenuSection(entry.getKey(), entry.getValue()));
        }
        return new Menu(menuId, name, sections);
    }
}
