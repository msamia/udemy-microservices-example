package com.example.common.v1.notification;

import com.example.common.v1.base.DomainEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationDomainEvent<T extends NotificationEvent> extends DomainEvent<T> {
    public static final String SOURCE = "account-core";

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({@JsonSubTypes.Type(
            name = "EmailTriggered",
            value = EmailTriggered.class
    )})
    public T getPayload() {
        return super.getPayload();
    }

}
