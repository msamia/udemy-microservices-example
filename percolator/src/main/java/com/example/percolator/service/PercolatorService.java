package com.example.percolator.service;

import com.example.common.v1.account.AccountCreated;
import com.example.common.v1.account.AccountDomainEvent;
import com.example.common.v1.account.AccountEvent;
import com.example.common.v1.book.BookCreated;
import com.example.common.v1.book.BookDomainEvent;
import com.example.common.v1.search_preference.SearchPreferenceCreated;
import com.example.common.v1.search_preference.SearchPreferenceDomainEvent;
import com.example.common.v1.search_preference.SearchPreferenceEvent;
import com.example.common.v1.search_preference.SearchPreferenceTriggered;
import com.example.percolator.config.PercolatorIndexFields;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.percolator.PercolateQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class PercolatorService {

    public static final String PERCOLATOR_INDEX = "percolator_index";
    private final Client esClient;

    public void save(SearchPreferenceDomainEvent<SearchPreferenceCreated> domainEvent) {
        SearchPreferenceCreated spc = domainEvent.getPayload();
        log.debug("Percolator Query saving process has been starting with {}", spc.toString());
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(spc);

        try {
            this.esClient
                    .prepareIndex(PERCOLATOR_INDEX, "docs", spc.getId())
                    .setSource(jsonBuilder().startObject().field(PercolatorIndexFields.QUERY.getFieldName(), boolQueryBuilder).endObject())
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                    .get();
        } catch (IOException e) {
            log.error("Error has occurred during SearchPreference saving process with event: {}", spc);
        }
    }

    public List<SearchPreferenceDomainEvent<SearchPreferenceTriggered>> findMatches(BookDomainEvent<BookCreated> bookCreatedDomainEvent) {
        try {
            BookCreated booCreated = bookCreatedDomainEvent.getPayload();
            // building our percolator query
            final XContentBuilder builder = jsonBuilder()
                    .startObject()
                    .field(PercolatorIndexFields.PRICE.getFieldName(), booCreated.getPrice().doubleValue())
                    .field(PercolatorIndexFields.BOOK_TYPE.getFieldName(), booCreated.getType())
                    .endObject();
            PercolateQueryBuilder percolateQueryBuilder = new PercolateQueryBuilder(
                    PercolatorIndexFields.QUERY.getFieldName(),
                    BytesReference.bytes(builder),
                    XContentType.JSON);

            //execute percolation
            SearchResponse searchResponse = this.esClient.prepareSearch(PERCOLATOR_INDEX)
                    .setQuery(percolateQueryBuilder)
                    .execute()
                    .actionGet();

            //find matches
            SearchHits hits = searchResponse.getHits();
            if (hits != null && hits.getHits().length != 0) {
                SearchHit[] matches = hits.getHits();
                return Arrays.stream(matches)
                        .peek(hit -> log.debug("Newly created book got matched on SearchPreference with id {}", hit.getId()))
                        .map(hit -> SearchPreferenceDomainEvent.<SearchPreferenceTriggered>builder()
                                .id(UUID.randomUUID())
                                .type(SearchPreferenceEvent.EventType.SEARCH_PREFERENCE_TRIGGERED.getEventName())
                                .aggregateId(UUID.randomUUID().toString())
                                .correlationId(bookCreatedDomainEvent.getId()) //chaining domain events
                                .created(Instant.now())
                                .source(SearchPreferenceDomainEvent.SOURCE)
                                .payload(new SearchPreferenceTriggered(hit.getId()))
                                .build())
                        .collect(Collectors.toList());
            } else {
                log.info("No hits had been found on newly created book with id {}", bookCreatedDomainEvent.getId());
            }
        } catch (IOException e) {
            log.error("Error has occurred during percolator query building process: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    private BoolQueryBuilder getBoolQueryBuilder(SearchPreferenceCreated spc) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (spc.getTypes() != null) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery(PercolatorIndexFields.BOOK_TYPE.getFieldName(), spc.getTypes()));
        }

        if (spc.getMinimumPrice() != null && spc.getMaximumPrice() != null) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(PercolatorIndexFields.PRICE.getFieldName()).gte(spc.getMinimumPrice().doubleValue()).lte(spc.getMaximumPrice().doubleValue()));
        } else if (spc.getMinimumPrice() != null) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(PercolatorIndexFields.PRICE.getFieldName()).gte(spc.getMinimumPrice().doubleValue()));
        } else if (spc.getMaximumPrice() != null) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(PercolatorIndexFields.PRICE.getFieldName()).gte(spc.getMaximumPrice().doubleValue()));
        }
        return boolQueryBuilder;
    }
}
