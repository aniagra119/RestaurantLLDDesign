package com.restaurant.builder;

import com.restaurant.model.Course;
import com.restaurant.model.Dish;
import com.restaurant.model.Menu;
import com.restaurant.model.MenuCourse;
import com.restaurant.model.MenuItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MenuBuilder {
    private final String id;
    private final String name;
    private final List<MenuCourseBuilder> courseBuilders = new ArrayList<>();

    public MenuBuilder(String name) {
        this("MENU-" + UUID.randomUUID().toString().substring(0, 8), name);
    }

    public MenuBuilder(String id, String name) {
        this.id = Objects.requireNonNull(id, "Menu ID cannot be null");
        this.name = Objects.requireNonNull(name, "Menu name cannot be null");
    }

    public static MenuBuilder create(String name) {
        return new MenuBuilder(name);
    }

    public MenuCourseBuilder addCourse(String courseName, int displayOrder) {
        MenuCourseBuilder courseBuilder = new MenuCourseBuilder(this, new Course("CRS-" + UUID.randomUUID().toString().substring(0, 6), courseName), displayOrder);
        this.courseBuilders.add(courseBuilder);
        return courseBuilder;
    }

    public Menu build() {
        Menu menu = new Menu(id, name);
        for (MenuCourseBuilder cb : courseBuilders) {
            menu.addMenuCourse(cb.buildCourse());
        }
        return menu;
    }

    public static class MenuCourseBuilder {
        private final MenuBuilder parent;
        private final Course course;
        private final int displayOrder;
        private final List<MenuItem> items = new ArrayList<>();

        public MenuCourseBuilder(MenuBuilder parent, Course course, int displayOrder) {
            this.parent = parent;
            this.course = course;
            this.displayOrder = displayOrder;
        }

        public MenuCourseBuilder addDish(Dish dish, double price) {
            return addDish(dish, BigDecimal.valueOf(price));
        }

        public MenuCourseBuilder addDish(Dish dish, BigDecimal price) {
            String itemId = "MI-" + UUID.randomUUID().toString().substring(0, 8);
            items.add(new MenuItem(itemId, dish, price));
            return this;
        }

        public MenuBuilder and() {
            return parent;
        }

        public Menu build() {
            return parent.build();
        }

        private MenuCourse buildCourse() {
            MenuCourse mc = new MenuCourse("MC-" + UUID.randomUUID().toString().substring(0, 8), course, displayOrder);
            for (MenuItem item : items) {
                mc.addItem(item);
            }
            return mc;
        }
    }
}

