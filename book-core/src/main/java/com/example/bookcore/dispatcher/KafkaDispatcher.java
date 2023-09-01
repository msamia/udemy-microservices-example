package com.example.bookcore.dispatcher;

import com.example.common.v1.book.BookDomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaDispatcher {

    private final KafkaTemplate<String, String> template;
    private final ObjectMapper om;

    @Value("${app.kafka.book.topic}")
    private String bookTopic;

    public void send(BookDomainEvent domainEvent) {
        String event = serialize(domainEvent);
        this.template.send(bookTopic, event);
        log.debug("Sending message {} to {}.", event, bookTopic);
    }

    private String serialize(Object event)  {
        try {
            return om.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.warn("Error has occurred during serialization: {}", e.getMessage());
            throw new RuntimeException("Error has occurred while serializing event " + event);
        }
    }
}
