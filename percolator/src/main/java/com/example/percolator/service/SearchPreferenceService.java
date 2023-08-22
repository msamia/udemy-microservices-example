package com.example.percolator.service;

import com.example.common.SearchPreferenceCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SearchPreferenceService {
    public void createPercolatorQuery(SearchPreferenceCreated spc) {
        log.debug("Percolator Query saving process has been starting with {}", spc.toString());
    }
}
