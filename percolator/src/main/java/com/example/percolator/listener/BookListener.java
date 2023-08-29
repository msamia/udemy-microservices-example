package com.example.percolator.listener;

import com.example.common.BookCreated;
import com.example.percolator.dispatcher.KafkaDispatcher;
import com.example.percolator.service.PercolatorService;
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
    private final PercolatorService service;
    private final KafkaDispatcher dispatcher;

    @KafkaListener(topics = "${app.kafka.book.topic}", clientIdPrefix = "${spring.kafka.consumer.client-id}-consumer")
    public void process(@Payload final byte[] payload) {
       Optional.ofNullable(payload)
               .ifPresentOrElse(event-> {
                   deserialize(event)
                           .ifPresent(bookCreated -> {
                               service.findMatches(bookCreated)
                                       .forEach(dispatcher::send);
                           });
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
