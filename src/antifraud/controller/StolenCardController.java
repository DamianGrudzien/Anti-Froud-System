package antifraud.controller;

import antifraud.model.StolenCard;
import antifraud.model.request.StolenCardRequest;
import antifraud.service.StolenCardService;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/antifraud")
public class StolenCardController {

    @Autowired
    StolenCardService stolenCardService;

    @PostMapping("/stolencard")
    @PreAuthorize("hasRole('SUPPORT')")
    public ResponseEntity<StolenCard> saveStolenCard(@Valid @RequestBody StolenCardRequest request) {
        StolenCard stolenCard = stolenCardService.saveStolenCard(request);
        return ResponseEntity.ok().body(stolenCard);

    }

    @DeleteMapping("/stolencard/{number}")
    @PreAuthorize("hasRole('SUPPORT')")
    public ResponseEntity<Map<String, String>> deleteStolenCard(@Valid @PathVariable String number) {
        stolenCardService.deleteByNumber(number);
        String value = "Card " + number + " successfully removed!";
        return ResponseEntity.status(HttpStatus.OK)
                             .body(Map.of("status", value));
    }

    @GetMapping("/stolencard")
    @PreAuthorize("hasRole('SUPPORT')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<StolenCard>> listOfStolenCards() {
        List<StolenCard> stolenCards = stolenCardService.finaAllStolenCards();
        return ResponseEntity.ok().body(stolenCards);
    }

}
