package com.restaurant.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class CustomerFeedback extends BaseEntity {
    private final CustomerVisit visit;
    private final int rating; // 1-5
    private final String comments;

    public CustomerFeedback(String feedbackId, CustomerVisit visit, int rating, String comments) {
        super(feedbackId);
        this.visit = Objects.requireNonNull(visit, "Visit cannot be null");
        this.rating = Math.max(1, Math.min(5, rating)); // Clamp between 1 and 5
        this.comments = comments != null ? comments : "";
    }

    public CustomerVisit getVisit() {
        return visit;
    }

    public int getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }
}
