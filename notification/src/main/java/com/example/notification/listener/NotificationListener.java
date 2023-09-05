package com.example.notification.listener;

import com.example.common.v1.notification.EmailTriggered;
import com.example.common.v1.notification.NotificationDomainEvent;
import com.example.common.v1.notification.NotificationEvent;
import com.example.notification.service.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

    private static final List<String> TRACKED = List.of(NotificationEvent.EventType.EMAIL_TRIGGERED.getEventName());
    private final ObjectMapper om;
    private final EmailService emailService;

    @KafkaListener(topics = "${app.kafka.notification.topic}", clientIdPrefix = "${spring.kafka.consumer.client-id}-consumer")
    public void process(@Payload final byte[] payload) {
       Optional.ofNullable(payload)
               .ifPresentOrElse(event-> {
                   try {
                       var jsonTree = om.readTree(payload);
                       var shouldProcessEvent = isTracked(jsonTree);
                       if (!shouldProcessEvent) {return;}

                       deserialize(jsonTree).ifPresent(notification -> {
                           if (NotificationEvent.EventType.EMAIL_TRIGGERED.getEventName().equals(notification.getType())) {
                               EmailTriggered emailRequest = notification.getPayload();
                               this.emailService.send(emailRequest.getEmail(), String.valueOf(emailRequest.getNotificationType()), "Email body");
                           }
                       });
                   } catch (Exception e) {
                       log.error("Error has occurred: {}", e.getMessage());
                   }}, () -> {throw new IllegalArgumentException("Payload cannot be null");
               });
    }

    private Optional<NotificationDomainEvent<EmailTriggered>> deserialize(final JsonNode payload) throws JsonProcessingException {
        JavaType javaType = om.constructType(new TypeReference<NotificationDomainEvent<EmailTriggered>>() {});
        return Optional.of(om.treeToValue(payload, javaType));
    }

    private boolean isTracked(final JsonNode payload) {
        var eventType = payload.findValue("type").textValue();
        return TRACKED.contains(eventType);
    }
}
