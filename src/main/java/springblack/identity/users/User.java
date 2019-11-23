package springblack.identity.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import springblack.common.Status;
import springblack.identity.organizations.Organization;
import springblack.identity.privileges.UserPrivilege;
import springblack.identity.roles.UserRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "email" }) })
public class User implements UserDetails {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID id;

    @JsonIgnore
    private UUID token;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    private Organization organization;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    private String email;
    private String username;

    @JsonIgnore
    private String password;

    @Transient
    private String newPassword;

    private String firstname;
    private String lastname;

    @CreationTimestamp
    private LocalDateTime stampCreated;
    public  LocalDateTime stampLastLogin;
    public  LocalDateTime stampConfirmSent;
    public  LocalDateTime stampConfirmed;

    @JsonIgnore
    private String passwordResetToken;

    @JsonIgnore
    @Column(nullable = true, unique = true, columnDefinition = "BINARY(16)")
    private UUID confirmEmailToken;

    @JsonIgnore
    private Boolean isConfirmed;

    @JsonIgnore
    private Boolean enabled = true;

    public List<String> getPrivileges() {

        List<String> authorities = new ArrayList<>();

        for (UserRole role : getRoles()) {

            authorities.add(role.getName());

            for (UserPrivilege privilege : role.getPrivileges()) {

                authorities.add(privilege.getName());

            }

        }

        return authorities;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles_links", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<UserRole> roles = new ArrayList<>();

}
