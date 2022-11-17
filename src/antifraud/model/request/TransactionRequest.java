package antifraud.model.request;

import antifraud.model.enums.Region;
import antifraud.validator.RegionCheck;
import lombok.Data;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;


@Data
@ToString
public class TransactionRequest {
    @NotNull
    @Positive
    Long amount;
    @NotEmpty
    String ip;
    @NotEmpty
    String number;
    @Enumerated(EnumType.STRING)
    @RegionCheck
    Region region;
    @PastOrPresent
    LocalDateTime date;
}
