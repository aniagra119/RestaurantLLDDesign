package com.restaurant.model;

import java.util.Objects;

public record Address(String street, String city, String state, String postalCode, String country) {
    public Address {
        Objects.requireNonNull(street, "Street must not be null");
        Objects.requireNonNull(city, "City must not be null");
    }

    public String getFormattedAddress() {
        return street + ", " + city + (state != null ? ", " + state : "") + (postalCode != null ? " - " + postalCode : "");
    }
}

