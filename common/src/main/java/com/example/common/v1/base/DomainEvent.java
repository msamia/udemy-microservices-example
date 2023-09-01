package com.example.common.v1.base;

import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(builderMethodName = "baseBuilder")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DomainEvent<T> implements EventContract<T> {
    private UUID id;
    private String source;
    private String type;
    private String version;
    private Instant created;
    private UUID correlationId;
    private String aggregateId;
    private T payload;

    public UUID getId() {
        return this.id;
    }

    public String getSource() {
        return this.source;
    }

    public String getVersion() {
        return this.version;
    }

    public Instant getCreated() {
        return this.created;
    }

    public UUID getCorrelationId() {
        return this.correlationId;
    }

    public String getAggregateId() {
        return this.aggregateId;
    }

    public String getType() {
        return this.type;
    }

    public T getPayload() {
        return this.payload;
    }
}
