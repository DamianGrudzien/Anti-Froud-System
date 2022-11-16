package antifraud.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class StolenCardRequest {
    @NotEmpty
    @CreditCardNumber
    String number;
}
