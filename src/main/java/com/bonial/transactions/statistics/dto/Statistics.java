package com.bonial.transactions.statistics.dto;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Statistics {
    private Lock lock = new ReentrantLock();
    private double sum;
    private double max;
    private double min;
    private long count;

    public Statistics(Double amount) {
        sum = amount;
        max = amount;
        min = amount;
        count++;
    }

    public void update(Double amount) {
        try {
            lock.lock();
            sum += amount;
            max = max > amount ? max : amount;
            min = min < amount ? min : amount;
            count++;
        } finally {
            lock.unlock();
        }
    }

    public Double getSum() {
        return sum;
    }

    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    public Long getCount() {
        return count;
    }
}
