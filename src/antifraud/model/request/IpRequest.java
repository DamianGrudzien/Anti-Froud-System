package antifraud.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class IpRequest {
    @NotEmpty
    String ip;
}
