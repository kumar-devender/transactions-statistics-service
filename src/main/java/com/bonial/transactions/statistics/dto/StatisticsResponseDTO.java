package com.bonial.transactions.statistics.dto;

import lombok.Data;


@Data
public class StatisticsResponseDTO {
    private double sum;
    private double avg;
    private double max;
    private double min;
    private long count;
}
