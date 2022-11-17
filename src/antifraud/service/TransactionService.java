package antifraud.service;

import antifraud.model.Transaction;
import antifraud.model.enums.Region;
import antifraud.model.enums.TransactionResult;
import antifraud.model.request.TransactionRequest;
import antifraud.model.response.TransactionResponse;
import antifraud.repository.IpRepository;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
        List<String> violationsMessages = new ArrayList<>();
        transactionRepository.save(modelMapper.map(transactionRequest, Transaction.class));

        var violationResult = checkAmount(transactionRequest.getAmount());

        if (violationResult == TransactionResult.PROHIBITED) {
            violationsMessages.add(amount);
        }
        if (stolenCardRepository.existsByNumber(transactionRequest.getNumber())) {
            violationsMessages.add(cardNumber);
            violationResult = TransactionResult.PROHIBITED;
        }
        if (ipRepository.existsByIp(transactionRequest.getIp())) {
            violationsMessages.add(ip);
            violationResult = TransactionResult.PROHIBITED;
        }

        List<Transaction> lastHourTransaction = transactionRepository.findByDateBetweenAndNumber(
                transactionRequest.getDate()
                                  .minusHours(1L), transactionRequest.getDate(), transactionRequest.getNumber());
        long listOfIpCorrelation = getIpCorrelation(transactionRequest, lastHourTransaction);
        long listOfRegionCorrelation = getRegionCorrelation(transactionRequest, lastHourTransaction);

        if (violationsMessages.isEmpty()) {
            log.info("Violation3: {}", violationResult);

            if (violationResult == TransactionResult.MANUAL_PROCESSING) {
                violationsMessages.add(amount);
                return new TransactionResponse(violationResult, getMessage(violationsMessages));
            } else {
                Optional<TransactionResult> resultOptional = getIpAndRegionCorrelation(violationsMessages,
                                                                                       listOfIpCorrelation,
                                                                                       listOfRegionCorrelation);
                if (resultOptional.isPresent()) {
                    violationResult = resultOptional.get();
                }
                if (violationsMessages.isEmpty()) {
                    violationsMessages.add(none);
                }
                return new TransactionResponse(violationResult, getMessage(violationsMessages));
            }
        } else {
            Optional<TransactionResult> resultOptional = getIpAndRegionCorrelation(violationsMessages,
                                                                                   listOfIpCorrelation,
                                                                                   listOfRegionCorrelation);
            if (resultOptional.isPresent()) {
                violationResult = resultOptional.get();
            }
            return new TransactionResponse(violationResult, getMessage(violationsMessages));
        }
    }

    private static long getIpCorrelation(TransactionRequest transactionRequest, List<Transaction> lastHourTransaction) {
        return lastHourTransaction.stream()
                                  .map(Transaction::getIp)
                                  .filter(tIp -> !tIp.equals(transactionRequest.getIp()))
                                  .distinct()
                                  .count();
    }

    private static long getRegionCorrelation(TransactionRequest transactionRequest, List<Transaction> lastHourTransaction) {
        return lastHourTransaction.stream()
                                  .map(Transaction::getRegion)
                                  .filter(region -> Arrays.asList(Region.values())
                                                          .contains(region))
                                  .filter(region -> region.compareTo(transactionRequest.getRegion()) != 0)
                                  .distinct()
                                  .count();
    }

    private String getMessage(List<String> violationsMessages) {
        return violationsMessages.stream()
                                 .sorted()
                                 .collect(Collectors.joining(", "));
    }

    private TransactionResult checkAmount(Long amount) {
        if (amount <= minTransaction) {
            return TransactionResult.ALLOWED;
        } else if (amount <= maxTransaction) {
            return TransactionResult.MANUAL_PROCESSING;
        } else {
            return TransactionResult.PROHIBITED;
        }
    }

    private Optional<TransactionResult> getIpAndRegionCorrelation(List<String> violationsMessages, long listOfIpCorrelation, long listOfRegionCorrelation) {
        TransactionResult trResult = null;
        if (listOfIpCorrelation == 2) {
            violationsMessages.add("ip-correlation");
            trResult = TransactionResult.MANUAL_PROCESSING;
        }
        if (listOfIpCorrelation > 2) {
            trResult = TransactionResult.PROHIBITED;
            violationsMessages.add("ip-correlation");
        }
        if (listOfRegionCorrelation == 2) {
            violationsMessages.add("region-correlation");
            trResult = TransactionResult.MANUAL_PROCESSING;
        }
        if (listOfRegionCorrelation > 2) {
            trResult = TransactionResult.PROHIBITED;
            violationsMessages.add("region-correlation");
        }
        if (trResult == null) {
            return Optional.empty();
        } else {
            return Optional.of(trResult);
        }
    }

}
