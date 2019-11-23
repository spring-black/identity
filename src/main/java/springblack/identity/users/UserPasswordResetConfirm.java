package springblack.identity.users;

import lombok.Data;

@Data
public class UserPasswordResetConfirm {

    private String token;
    private String password;

}
