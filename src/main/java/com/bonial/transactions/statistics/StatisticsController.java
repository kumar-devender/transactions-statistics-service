package com.bonial.transactions.statistics;

import com.bonial.transactions.statistics.dto.StatisticsResponseDTO;
import com.bonial.transactions.statistics.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1")
public class StatisticsController {
    private final TransactionService transactionService;

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public StatisticsResponseDTO getStatistics() {
        return transactionService.getStatistics();
    }

}
