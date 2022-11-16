package antifraud.model.request;

import antifraud.model.enums.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Data
public class UserAccessChange {
    @NotEmpty
    String username;
    UserStatus operation;
}
