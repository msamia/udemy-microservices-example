package com.example.common;

import java.math.BigDecimal;

public record BookCreated(String id, String title, String author, BigDecimal price, BookType type) {
}
