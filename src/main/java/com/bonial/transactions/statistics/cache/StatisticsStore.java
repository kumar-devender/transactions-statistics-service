package com.bonial.transactions.statistics.cache;

import com.bonial.transactions.statistics.dto.Statistics;
import com.bonial.transactions.statistics.dto.StatisticsDTO;
import com.bonial.transactions.statistics.dto.TransactionDTO;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toMap;

@Component
public class StatisticsStore {
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy MM dd : HH:mm:ss");
    private static final int CACHE_INVALIDATION_DURATION_IN_SECOND = 60;
    private LoadingCache<String, Statistics> cache;

    @PostConstruct
    public void init() {
        CacheLoader<String, Statistics> loader = new CacheLoader<String, Statistics>() {
            @Override
            public Statistics load(String key) throws ExecutionException {
                return cache.get(key);
            }
        };
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(CACHE_INVALIDATION_DURATION_IN_SECOND, TimeUnit.SECONDS)
                .build(loader);
    }

    public void store(TransactionDTO transactionDTO) {
        String key = Instant.ofEpochMilli(transactionDTO.getTimestamp()).atZone(UTC_ZONE_ID).toLocalDateTime().format(DATE_TIME_FORMATTER);
        synchronized (key) {
            Statistics statistics = cache.getIfPresent(key);
            if (statistics == null) {
                Statistics newStatistics = new Statistics(transactionDTO.getAmount().doubleValue());
                cache.put(key, newStatistics);
            } else {
                statistics.update(transactionDTO.getAmount().doubleValue());
            }
        }
    }

    public StatisticsDTO getStatistics(Instant currentTime) {
        Map<String, Statistics> statistics = cache.asMap().entrySet()
                .parallelStream()
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        StatisticsDTO statisticsDTO = new StatisticsDTO();

        statistics.entrySet()
                .stream()
                .filter(entry -> isActive(currentTime, entry.getKey()))
                .forEach(entry -> updateSummary(statisticsDTO, entry.getValue()));

        statisticsDTO.setAvg(statisticsDTO.getSum() / statisticsDTO.getCount());
        return statisticsDTO;
    }

    private boolean isActive(Instant currentTime, String storedKey) {
        LocalDateTime localDateTime = LocalDateTime.parse(storedKey, DATE_TIME_FORMATTER);
        return localDateTime.isAfter(currentTime.atZone(UTC_ZONE_ID).toLocalDateTime().minusMinutes(1));
    }

    private void updateSummary(StatisticsDTO statisticsDTO, Statistics statistics) {
        double sum = statisticsDTO.getSum() + statistics.getSum();
        double max = statisticsDTO.getMax() > statistics.getMax() ? statisticsDTO.getMax() : statistics.getMax();
        double min = statisticsDTO.getMin() < statistics.getMin() ? statisticsDTO.getMin() : statistics.getMin();
        long count = statisticsDTO.getCount() + statistics.getCount();
        statisticsDTO.setCount(statisticsDTO.getCount() + 1);
        statisticsDTO.setMax(max);
        statisticsDTO.setMin(min);
        statisticsDTO.setSum(sum);
        statisticsDTO.setCount(count);
    }
}