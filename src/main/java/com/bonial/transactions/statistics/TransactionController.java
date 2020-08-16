package com.bonial.transactions.statistics;

import com.bonial.transactions.statistics.dto.TransactionDTO;
import com.bonial.transactions.statistics.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/v1")
public class TransactionController {
    private final TransactionService transactionService;

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<TransactionDTO> create(@RequestBody @Valid TransactionDTO transactionDTO) {
        HttpStatus httpStatus = transactionService.create(transactionDTO) ? HttpStatus.CREATED : HttpStatus.NO_CONTENT;
        return ResponseEntity.status(httpStatus).body(transactionDTO);
    }
}
