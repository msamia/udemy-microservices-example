package com.example.percolator.dispatcher;

import com.example.common.SearchPreferenceTriggered;
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

    @Value("${app.kafka.search-preference.triggered.topic}")
    private String searchPreferenceTopic;

    public void send(SearchPreferenceTriggered spt) {
        String event = serialize(spt);
        this.template.send(searchPreferenceTopic, event);
        log.debug("Sending message {} to {}.", event, searchPreferenceTopic);
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
