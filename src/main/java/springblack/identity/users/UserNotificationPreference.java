package springblack.identity.users;

import lombok.Data;

@Data
public class UserNotificationPreference {

    private String  preferenceName;
    private Boolean enabled;

}
