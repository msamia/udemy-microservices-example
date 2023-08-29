package com.example.percolator.service;

import com.example.common.BookCreated;
import com.example.common.SearchPreferenceCreated;
import com.example.common.SearchPreferenceTriggered;
import com.example.percolator.config.PercolatorIndexFields;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    public void save(SearchPreferenceCreated spc) {
        log.debug("Percolator Query saving process has been starting with {}", spc.toString());
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(spc);

        try {
            this.esClient
                    .prepareIndex(PERCOLATOR_INDEX, "docs", spc.id())
                    .setSource(jsonBuilder().startObject().field(PercolatorIndexFields.QUERY.getFieldName(), boolQueryBuilder).endObject())
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                    .get();
        } catch (IOException e) {
            log.error("Error has occurred during SearchPreference saving process with event: {}", spc);
        }
    }

    public List<SearchPreferenceTriggered> findMatches(BookCreated bookCreated) {
        try {
            // building our percolator query
            final XContentBuilder builder = jsonBuilder()
                    .startObject()
                    .field(PercolatorIndexFields.PRICE.getFieldName(), bookCreated.price())
                    .field(PercolatorIndexFields.BOOK_TYPE.getFieldName(), bookCreated.type())
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
            if (hits != null) {
                SearchHit[] matches = hits.getHits();
                return Arrays.stream(matches)
                        .map(hit -> new SearchPreferenceTriggered(hit.getId()))
                        .collect(Collectors.toList());
            } else {
                log.info("No hits had been found on newly created book with id {}", bookCreated.id());
            }
        } catch (IOException e) {
            log.error("Error has occurred during percolator query building process: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    private BoolQueryBuilder getBoolQueryBuilder(SearchPreferenceCreated spc) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (spc.types() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery(PercolatorIndexFields.BOOK_TYPE.getFieldName(), spc.types()));
        }

        if (spc.minimumPrice() != null && spc.maximumPrice() != null) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(PercolatorIndexFields.PRICE.getFieldName()).gte(spc.minimumPrice().doubleValue()).lte(spc.maximumPrice().doubleValue()));
        } else if (spc.minimumPrice() != null) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(PercolatorIndexFields.PRICE.getFieldName()).gte(spc.minimumPrice().doubleValue()));
        } else if (spc.maximumPrice() != null) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(PercolatorIndexFields.PRICE.getFieldName()).gte(spc.maximumPrice().doubleValue()));
        }
        return boolQueryBuilder;
    }
}
