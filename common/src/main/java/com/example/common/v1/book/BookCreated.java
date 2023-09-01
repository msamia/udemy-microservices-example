package com.example.common.v1.book;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonTypeName("BookCreated")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookCreated extends BookEvent {

    private String id;
    private String title;
    private String author;
    private BigDecimal price;
    private BookType type;

    @JsonCreator
    public BookCreated(String id, String title, String author, BigDecimal price, BookType type) {
        super(EventType.BOOK_CREATED);
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.type = type;
    }

}
