package antifraud.controller;

import antifraud.model.enums.TransactionResult;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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