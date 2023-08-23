package com.example.percolator.service;

import com.example.common.SearchPreferenceCreated;
import com.example.percolator.config.PercolatorIndexFields;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchPreferenceService {

    private final Client esClient;

    public void save(SearchPreferenceCreated spc) {
        log.debug("Percolator Query saving process has been starting with {}", spc.toString());
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(spc);

        try {
            this.esClient
                    .prepareIndex("percolator_index", "docs", spc.id())
                    .setSource(jsonBuilder().startObject().field(PercolatorIndexFields.QUERY.getFieldName(), boolQueryBuilder).endObject())
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                    .get();
        } catch (IOException e) {
            log.error("Error has occurred during SearchPreference saving process with event: {}", spc);
        }
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
