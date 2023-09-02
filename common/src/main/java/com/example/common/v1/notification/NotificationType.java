package com.example.common.v1.notification;

public enum NotificationType {
    SEARCH_PREFERENCE_HIT("EMAIL");

    private String type;
    NotificationType(String type) {
        this.type = type;
    }
}
