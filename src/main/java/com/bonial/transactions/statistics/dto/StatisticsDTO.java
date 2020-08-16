package com.bonial.transactions.statistics.dto;

import lombok.Data;


@Data
public class StatisticsDTO {
    private double sum;
    private double avg;
    private double max;
    private double min = Double.MAX_VALUE;
    private long count;
}
