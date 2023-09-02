package com.example.common.v1.search_preference;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.beans.ConstructorProperties;
import lombok.Getter;

@Getter
@JsonTypeName("SearchPreferenceTriggered")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchPreferenceTriggered extends SearchPreferenceEvent {

    private String id;

    @JsonCreator
    @ConstructorProperties({"id"})
    public SearchPreferenceTriggered(String id) {
        super(EventType.SEARCH_PREFERENCE_TRIGGERED);
        this.id = id;
    }
}
