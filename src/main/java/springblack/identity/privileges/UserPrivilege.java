package springblack.identity.privileges;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import springblack.identity.roles.UserRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "users_privileges")
public class UserPrivilege {

    public static final String PRIVILEGE_ROOT_ALL                     = "root.all";
    public static final String PRIVILEGE_ADMIN_ALL                    = "admin.all";
    public static final String PRIVILEGE_USERS_LOGIN                  = "users.login";
    public static final String PRIVILEGE_USERS_DELETE                 = "users.delete";
    public static final String PRIVILEGE_USERS_CREATE_BYPRINCIPAL     = "users.create.byprincipal";
    public static final String PRIVILEGE_USERS_GET_BYID               = "users.get.byid";
    public static final String PRIVILEGE_USERS_SEARCH_BYORG           = "users.search.byorg";
    public static final String PRIVILEGE_USERS_PROFILE_GET            = "users.profile.get";
    public static final String PRIVILEGE_USERS_PROFILE_UPDATE         = "users.profile.update";
    public static final String PRIVILEGE_USERS_PROFILE_CHANGEPASSWORD = "users.profile.changepassword";
    public static final String PRIVILEGE_USERS_ROLES_GET              = "users.roles.get";
    public static final String PRIVILEGE_USERS_ROLES_CREATE           = "users.roles.create";
    public static final String PRIVILEGE_USERS_ROLES_DELETE           = "users.roles.delete";
    public static final String PRIVILEGE_USERS_ROLES_ASSIGN           = "users.roles.assign";
    public static final String PRIVILEGE_USERS_PRIVILEGES_CREATE      = "users.privileges.create";
    public static final String PRIVILEGE_USERS_PRIVILEGES_DELETE      = "users.privileges.delete";
    public static final String PRIVILEGE_USERS_PRIVILEGES_UPDATE      = "users.privileges.update";
    public static final String PRIVILEGE_USERS_PRIVILEGES_ASSIGN      = "users.privileges.assign";
    public static final String PRIVILEGE_USERS_PRIVILEGES_SEARCH      = "users.privileges.search";
    public static final String PRIVILEGE_USERS_ROLES_ORG_SEARCH       = "users.roles.org.search";
    public static final String PRIVILEGE_USERS_ROLES_ORG_GET_BYID     = "users.roles.org.get.byname";
    public static final String PRIVILEGE_USERS_ROLES_ORG_GET_BYNAME   = "users.roles.org.get.byid";
    public static final String PRIVILEGE_USERS_ROLES_ORG_CREATE       = "users.roles.org.create";
    public static final String PRIVILEGE_USERS_ROLES_ORG_DELETE       = "users.roles.org.delete";
    public static final String PRIVILEGE_USERS_ROLES_ORG_UPDATE       = "users.roles.org.update";
    public static final String PRIVILEGE_USERS_MANAGE_ROLES_GET       = "users.manage.roles.get";
    public static final String PRIVILEGE_USERS_MANAGE_ROLES_ADD       = "users.manage.roles.add";
    public static final String PRIVILEGE_USERS_MANAGE_ROLES_DELETE    = "users.manage.roles.delete";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true)
    private String name;
    private String friendlyName;
    private String description;

    @JsonBackReference
    @ManyToMany(mappedBy = "privileges")
    @JsonIgnoreProperties("privileges")
    private List<UserRole> roles;

    @CreationTimestamp
    private LocalDateTime stampCreated;

    @UpdateTimestamp
    private LocalDateTime stampUpdated;

    public UserPrivilege(String name) {

        this.name = name;

    }

}
