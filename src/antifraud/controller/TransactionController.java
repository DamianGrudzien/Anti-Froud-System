package antifraud.controller;

import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PreAuthorize("hasRole('MERCHANT')")
    @PostMapping("/api/antifraud/transaction")
    public TransactionResponse checkTransaction(@Valid @RequestBody TransactionRequest transactionRequest) {
        return transactionService.processTransaction(transactionRequest);
    }

}