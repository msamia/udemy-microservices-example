package com.example.common.v1.book;

import com.example.common.v1.base.DomainEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookDomainEvent<T extends BookEvent> extends DomainEvent<T> {
    public static final String SOURCE = "book-core";

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({@JsonSubTypes.Type(
            name = "BookCreated",
            value = BookCreated.class
    )})
    public T getPayload() {
        return super.getPayload();
    }

}
