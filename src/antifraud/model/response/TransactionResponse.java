package antifraud.model.response;

import antifraud.model.enums.TransactionResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    TransactionResult result;
    String info;
}
