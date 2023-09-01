package com.example.common.v1.search_preference;

import com.example.common.v1.account.AccountCreated;
import com.example.common.v1.base.DomainEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchPreferenceDomainEvent<T extends SearchPreferenceEvent> extends DomainEvent<T> {
    public static final String SOURCE = "search-preference";

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({@JsonSubTypes.Type(
            name = "SearchPreferenceCreated",
            value = SearchPreferenceCreated.class
    ),@JsonSubTypes.Type(
            name = "SearchPreferenceTriggered",
            value = SearchPreferenceTriggered.class
    )})
    public T getPayload() {
        return super.getPayload();
    }

}
