package com.bonial.transactions.statistics.service;

import com.bonial.transactions.statistics.cache.StatisticsStore;
import com.bonial.transactions.statistics.dto.StatisticsDTO;
import com.bonial.transactions.statistics.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final StatisticsStore statisticsStore;
    private static int EXPIRED_TRANSACTION_TIME_IN_MINUTES = 1;

    public boolean create(TransactionDTO transactionDTO) {
        if (isExpired(transactionDTO.getTimestamp())) {
            log.info("Transaction is expired. Skipping it ...");
            return false;
        }
        statisticsStore.store(transactionDTO);
        log.info("Added transaction in the store");
        return true;
    }

    public StatisticsDTO getStatistics() {
        Instant currentTime = Instant.now();
        return statisticsStore.getStatistics(currentTime);
    }

    private boolean isExpired(long transactionTimestamp) {
        Instant instant = Instant.ofEpochMilli(transactionTimestamp);
        return instant.isBefore(Instant.now().minus(EXPIRED_TRANSACTION_TIME_IN_MINUTES, ChronoUnit.MINUTES));
    }
}
