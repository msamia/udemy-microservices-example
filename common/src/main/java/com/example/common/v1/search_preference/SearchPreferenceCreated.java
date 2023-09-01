package com.example.common.v1.search_preference;

import com.example.common.v1.book.BookType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
@JsonTypeName("SearchPreferenceCreated")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchPreferenceCreated extends SearchPreferenceEvent {

    private String id;
    private String title;
    private String author;
    private BigDecimal minimumPrice;
    private BigDecimal maximumPrice;
    private BookType[] types;

    @JsonCreator
    @ConstructorProperties({"id", "title", "author", "minimumPrice", "maximumPrice", "types"})
    public SearchPreferenceCreated(String id, String title, String author, BigDecimal minimumPrice, BigDecimal maximumPrice, BookType[] types) {
        super(EventType.SEARCH_PREFERENCE_CREATED);
        this.id = id;
        this.title = title;
        this.author = author;
        this.minimumPrice = minimumPrice;
        this.maximumPrice = maximumPrice;
        this.types = types;
    }
}
