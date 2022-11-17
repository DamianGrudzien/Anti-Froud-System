package antifraud.model.request;

import antifraud.model.enums.Role;
import antifraud.model.enums.UserStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserRequest {
    @NotEmpty
    String name;
    @NotEmpty
    String username;
    @NotEmpty
    String password;
    Role role;
    UserStatus userStatus;
}
