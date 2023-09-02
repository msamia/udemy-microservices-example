package com.example.common.v1.notification;

import com.example.common.v1.account.AccountCreated;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.NonNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        visible = true,
        property = "eventType",
        defaultImpl = NotificationEvent.DefaultNotificationEvent.class
)
@JsonSubTypes({@JsonSubTypes.Type(EmailTriggered.class)})
public abstract class NotificationEvent {

    @NonNull
    protected String eventType;

    public NotificationEvent(EventType eventType) {
        this.eventType = eventType.getEventName();
    }

    private NotificationEvent() {
    }

    @JsonIgnore
    public String getEventName() {
        return this.eventType;
    }

    public static enum EventType {
        EMAIL_TRIGGERED("EmailTriggered");

        private final String eventName;

        EventType(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return this.eventName;
        }
    }

    protected static class DefaultNotificationEvent extends NotificationEvent {
        protected DefaultNotificationEvent() {
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof DefaultNotificationEvent)) {
                return false;
            } else {
                DefaultNotificationEvent other = (DefaultNotificationEvent)o;
                if (!other.canEqual(this)) {
                    return false;
                } else {
                    return super.equals(o);
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof DefaultNotificationEvent;
        }

        public int hashCode() {
            int result = super.hashCode();
            return result;
        }
    }
}
