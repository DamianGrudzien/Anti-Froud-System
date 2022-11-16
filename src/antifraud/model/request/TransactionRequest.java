package antifraud.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;


@Data
public class TransactionRequest {
    @NotNull
    @Positive
    Long amount;
    @NotEmpty
    String ip;
    @NotEmpty
    String number;
    @NotEmpty
    String region;
    LocalDateTime date;
}
