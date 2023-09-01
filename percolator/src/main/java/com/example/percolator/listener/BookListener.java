package com.example.percolator.listener;

import com.example.common.v1.book.BookCreated;
import com.example.common.v1.book.BookDomainEvent;
import com.example.common.v1.search_preference.SearchPreferenceDomainEvent;
import com.example.percolator.dispatcher.KafkaDispatcher;
import com.example.percolator.service.PercolatorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
public class BookListener {

    private static final List<String> TRACKED = List.of("BookCreated");
    private final ObjectMapper om;
    private final PercolatorService service;
    private final KafkaDispatcher dispatcher;

    @KafkaListener(topics = "${app.kafka.book.topic}", clientIdPrefix = "${spring.kafka.consumer.client-id}-consumer")
    public void process(@Payload final byte[] payload) {

       Optional.ofNullable(payload)
               .ifPresentOrElse(event-> {
                   try {
                       var jsonTree = om.readTree(payload);
                       var shouldProcessEvent = isTracked(jsonTree);
                       if (!shouldProcessEvent) {return;}
                       deserialize(jsonTree).ifPresent(bookCreated -> {
                                   service.findMatches(bookCreated)
                                           .forEach(spte -> dispatcher.send((SearchPreferenceDomainEvent) spte));
                               });
                   } catch (IOException e) {
                       log.error("Error has occurred: {}", e.getMessage());
                   }}, () -> {throw new IllegalArgumentException("Payload cannot be null");
               });
    }

    private Optional<BookDomainEvent> deserialize(final JsonNode payload) throws JsonProcessingException {
        JavaType javaType = om.constructType(new TypeReference<BookDomainEvent<BookCreated>>() {});
        return Optional.of(om.treeToValue(payload, javaType));
    }

    private boolean isTracked(final JsonNode payload) {
        var eventType = payload.findValue("type").textValue();
        return TRACKED.contains(eventType);
    }
}
