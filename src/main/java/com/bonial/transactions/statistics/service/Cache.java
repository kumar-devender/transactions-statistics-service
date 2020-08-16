package com.bonial.transactions.statistics.service;


import java.util.LinkedHashMap;
import java.util.Map;

public class Cache<LocalDateTime, StatisticsDTO> extends LinkedHashMap<LocalDateTime, StatisticsDTO> {
    @Override
    protected boolean removeEldestEntry(Map.Entry<LocalDateTime, StatisticsDTO> entry) {
        LocalDateTime key = entry.getKey();
        StatisticsDTO value = entry.getValue();
        return false;
    }
}
