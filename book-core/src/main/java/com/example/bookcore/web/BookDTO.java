package com.example.bookcore.web;

import com.example.common.BookType;
import java.math.BigDecimal;

public record BookDTO(String title, String author, BigDecimal price, BookType type) {
}
