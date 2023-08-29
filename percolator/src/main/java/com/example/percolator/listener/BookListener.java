package com.example.percolator.listener;

import com.example.common.BookCreated;
import com.example.common.SearchPreferenceCreated;
import com.example.percolator.service.SearchPreferenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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

    private final ObjectMapper om;
    private final SearchPreferenceService service;

    @KafkaListener(topics = "${app.kafka.book.topic}", clientIdPrefix = "${spring.kafka.consumer.client-id}-consumer")
    public void process(@Payload final byte[] payload) {
       Optional.ofNullable(payload)
               .ifPresentOrElse(event-> {
                   deserialize(event)
                           .ifPresent(System.out::println);
               },
                       () -> {throw new IllegalArgumentException("Payload cannot be null");
               });

    }

    private Optional<BookCreated> deserialize(byte[] spc) {
        try {
            return Optional.of(om.readValue(spc, BookCreated.class));
        } catch (IOException e) {
            log.error("Error has occurred {}", e.getMessage());
        }
        return Optional.empty();
    }
}
