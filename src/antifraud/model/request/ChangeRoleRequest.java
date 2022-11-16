package antifraud.model.request;

import antifraud.model.enums.Role;
import lombok.Getter;

@Getter
public class ChangeRoleRequest {
    String username;
    Role role;
}
