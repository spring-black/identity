package springblack.identity.roles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import springblack.identity.organizations.Organization;
import springblack.identity.privileges.UserPrivilege;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "users_roles")
@JsonIgnoreProperties({ "users" })
public class UserRole {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToMany
    @JoinTable(name = "users_roles_organizations_links", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "organization_id", referencedColumnName = "id"))
    private List<Organization> organizations;

    @Column(unique = true)
    private String name;
    private String friendlyName;
    private String description;

//    @JsonIgnore
//    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
//    private List<User> users;

    @ManyToMany
    @JoinTable(name = "users_roles_privileges_links", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private List<UserPrivilege> privileges;

    @CreationTimestamp
    private LocalDateTime stampCreated;

    @UpdateTimestamp
    private LocalDateTime stampUpdated;

    public UserRole(String name) {

        this.name = name;

    }

    public UserRole(String name, List<UserPrivilege> privileges) {

        this.name = name;
        this.privileges = privileges;

    }

}
