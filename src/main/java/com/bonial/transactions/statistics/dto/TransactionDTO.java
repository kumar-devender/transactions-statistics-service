package com.bonial.transactions.statistics.dto;

import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDTO {
    @NotNull
    @DecimalMin("1.0")
    private BigDecimal amount;
    @NotNull
    private Long timestamp;
}
