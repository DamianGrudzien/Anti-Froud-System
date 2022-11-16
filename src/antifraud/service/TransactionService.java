package antifraud.service;

import antifraud.model.Transaction;
import antifraud.model.enums.Region;
import antifraud.model.enums.TransactionResult;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.repository.IpRepository;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TransactionService {
    @Autowired
    StolenCardRepository stolenCardRepository;
    @Autowired
    IpRepository ipRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    TransactionRepository transactionRepository;
    @Value("${transaction.service.amount}")
    private String amount;
    @Value("${transaction.service.card-number}")
    private String cardNumber;
    @Value("${transaction.service.ip}")
    private String ip;
    @Value("${transaction.service.none}")
    private String none;
    @Value("${transaction.service.minTransaction}")
    private int minTransaction;
    @Value("${transaction.service.maxTransaction}")
    private int maxTransaction;

    public TransactionResponse processTransaction(TransactionRequest transactionRequest) {
        TransactionResponse transactionResponse = new TransactionResponse();
        List<String> info = new ArrayList<>();

        transactionRepository.save(modelMapper.map(transactionRequest, Transaction.class));

        if (stolenCardRepository.existsByNumber(transactionRequest.getNumber())) {
            info.add(cardNumber);
            transactionResponse.setResult(TransactionResult.PROHIBITED);
        }
        if (ipRepository.existsByIp(transactionRequest.getIp())) {
            transactionResponse.setResult(TransactionResult.PROHIBITED);
            info.add(ip);
        }


        if (transactionRequest.getAmount() <= minTransaction) {
            transactionResponse.setResult(TransactionResult.ALLOWED);
            info.add(none);
        } else if (transactionRequest.getAmount() <= maxTransaction && transactionResponse.getResult() != TransactionResult.PROHIBITED) {
            transactionResponse.setResult(TransactionResult.MANUAL_PROCESSING);
            info.add(amount);
        } else if (transactionResponse.getResult() != TransactionResult.PROHIBITED || (transactionRequest.getAmount() > maxTransaction && transactionResponse.getResult() == TransactionResult.PROHIBITED)) {
            transactionResponse.setResult(TransactionResult.PROHIBITED);
            info.add(amount);
        }

        List<Transaction> lastHourTransaction = transactionRepository.findByDateBetweenAndNumber(
                transactionRequest.getDate()
                                  .minusHours(1L), transactionRequest.getDate(), transactionRequest.getNumber());

        long listOfIpCorrelation = lastHourTransaction.stream()
                                                      .filter(t -> t.getIp()
                                                                    .equals(transactionRequest.getIp()))
                                                      .count();
        long listOfRegionCorrelation = lastHourTransaction.stream()
                                                          .filter(t -> Arrays.asList(Region.values())
                                                                             .contains(t))
                                                          .count();
        if (listOfIpCorrelation == 3) {
            transactionResponse.setResult(TransactionResult.MANUAL_PROCESSING);
            info.add("ip-correlation");
        } else if (listOfIpCorrelation > 3) {
            transactionResponse.setResult(TransactionResult.PROHIBITED);
            info.add("ip-correlation");
        }

        if (listOfRegionCorrelation == 3) {
            transactionResponse.setResult(TransactionResult.MANUAL_PROCESSING);
            info.add("region-correlation");
        } else if (listOfRegionCorrelation > 3) {
            transactionResponse.setResult(TransactionResult.PROHIBITED);
            info.add("region-correlation");
        }


        transactionResponse.setInfo(info.stream()
                                        .sorted()
                                        .collect(Collectors.joining(", ")));
        return transactionResponse;
    }
}
