package com.example.common.v1.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

@Getter
@JsonTypeName("EmailTriggered")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailTriggered extends NotificationEvent {

    private NotificationType notificationType;
    private String email;
    private String lastName;
    private String firstName;

    @JsonCreator
    public EmailTriggered(String lastName, String firstName, String email, NotificationType type) {
        super(EventType.EMAIL_TRIGGERED);
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.notificationType = type;
    }

}
