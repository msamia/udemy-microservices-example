package com.example.common;

import java.math.BigDecimal;

public record SearchPreferenceCreated(String id, String title, String author, BigDecimal minimumPrice, BigDecimal maximumPrice, BookType[] types) {
}
