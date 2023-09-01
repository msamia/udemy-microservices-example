package com.example.common.v1.base;

import java.time.Instant;
import java.util.UUID;

public interface EventContract<T> {

    String VERSION = "1.0";

    /**
     * [Required] uuid of the event
     *
     * @return uuid
     */
    UUID getId();

    /**
     * [Required] unique identifier of the source system of the event
     *
     * @return source identifier
     */
    String getSource();

    /**
     * [Required] a field that characterizes the event - always provides a verb in past tense since the event describes something that has
     * previously happened
     *
     * @return event type identifier
     */
    String getType();

    /**
     * [Required] event format version (The payload should be versioned separately in the payload structure if necessary.
     *
     * @return contract version
     */
    String getVersion();

    /**
     * [Required] UTC timestamp of when the event was created.
     *
     * @return creation timestamp
     */
    Instant getCreated();

    /**
     * [Required] This UUID can be used to chain logically connected events together. (Usually a domain event triggers changes in other
     * domains and generates other domain events. In such case these events would contain the same correlationId) <b><br>
     * <code>correlationId</code> must always equal to the <code>id</code> of the first message in its chain.</b>
     *
     * @return correlation id
     */
    UUID getCorrelationId();

    /**
     * [Required] the actual content of the event, service specific
     *
     * @return payload
     */
    T getPayload();

    /**
     * [Optional] Messages have the same aggregate Id should be processed in order. (Maintaining consistency in case of the out of order
     * processing is the consumer's responsibility.)
     *
     * @return aggregate id
     */
    String getAggregateId();

}
