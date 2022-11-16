package antifraud.service;

import antifraud.model.StolenCard;
import antifraud.model.request.StolenCardRequest;
import antifraud.repository.StolenCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StolenCardService {
    private static final String CARD_REGEX =
            "^(?:4[0-9]{12}(?:[0-9]{3})?|[25][1-7][0-9]{14}|6(?:011|5[0-9][0-9])[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|" +
                    "[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})$";

    private static final Pattern patternCard = Pattern.compile(CARD_REGEX);

    @Autowired
    StolenCardRepository stolenCardRepository;

    public StolenCard saveStolenCard(StolenCardRequest request) {
        if (stolenCardRepository.existsByNumber(request.getNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        StolenCard stolenCard = new StolenCard();
        stolenCard.setNumber(request.getNumber());
        return stolenCardRepository.save(stolenCard);
    }

    @Transactional
    public Long deleteByNumber(String number) {
        Matcher matcherCard = patternCard.matcher(number);
        if (!matcherCard.matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (!stolenCardRepository.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return stolenCardRepository.deleteByNumber(number);
    }

    public List<StolenCard> finaAllStolenCards() {
        return stolenCardRepository.findByOrderByIdAsc();
    }
}
