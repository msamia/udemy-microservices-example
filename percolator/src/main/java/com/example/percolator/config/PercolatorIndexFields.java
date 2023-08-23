package com.example.percolator.config;

import lombok.Getter;

@Getter
public enum PercolatorIndexFields {

    BOOK_TYPE("bookType", "keyword"),
    PRICE("price", "double"),
    QUERY("query", "percolator");

    private String fieldName;
    private String fieldType;

    PercolatorIndexFields(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }
}
