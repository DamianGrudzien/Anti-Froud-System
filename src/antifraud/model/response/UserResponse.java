package antifraud.model.response;

import antifraud.model.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {
    Long id;
    String name;
    String username;
    Role role;
}
