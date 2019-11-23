package springblack.identity.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springblack.common.Status;
import springblack.common.exceptions.PermissionDeniedException;
import springblack.common.exceptions.PrincipalNotFoundException;
import springblack.common.exceptions.ResourceNotFoundException;
import springblack.identity.organizations.Organization;
import springblack.identity.organizations.OrganizationsService;
import springblack.identity.roles.UserRolesService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    @Lazy
    private OrganizationsService organizationsService;

    @Autowired
    @Lazy
    private UserRolesService userRolesService;

    public User getById(UUID id) {

        Optional<User> optionalUser = usersRepository.findById(id);

        if (optionalUser.isPresent()) {

            return optionalUser.get();

        } else {

            throw new ResourceNotFoundException("user could not be located");

        }

    }

    public User getByIdAndPrincipalOrganization(UUID id, Principal principal) {

        Optional<User> optionalUser = usersRepository.getByOrganizationAndId(getByPrincipal(principal).getOrganization(), id);

        if (optionalUser.isPresent()) {

            return optionalUser.get();

        } else {

            throw new ResourceNotFoundException("user could not be located");

        }

    }

    public User getByEmail(String email) {

        Optional<User> optionalUser = usersRepository.getByEmail(email);

        if (optionalUser.isPresent()) {

            return optionalUser.get();

        } else {

            throw new ResourceNotFoundException("user could not be located");

        }
    }

    public User getByConfirmEmailToken(UUID token) {

        Optional<User> optionalUser = usersRepository.getByConfirmEmailToken(token);

        if (optionalUser.isPresent()) {

            return optionalUser.get();

        } else {

            throw new ResourceNotFoundException("could not locate confirmation token");

        }

    }

    public Boolean confirmByToken(UUID token, UserPassword userPassword) {

        User user = getByConfirmEmailToken(token);

        user.setPassword(passwordEncoder.encode(userPassword.getPassword()));
        user.setStampConfirmed(LocalDateTime.now());
        user.setConfirmEmailToken(null);

        usersRepository.save(user);

        return true;

    }

    public void confirmResend(UUID id, Principal principal) {

        User user = getByIdAndPrincipalOrganization(id, principal);

        user.setConfirmEmailToken(UUID.randomUUID());
        user.setStampConfirmSent(LocalDateTime.now());

        usersRepository.save(user);

    }

    public User createByPrincipal(User user, Principal principal) {

        User principalUser = getByPrincipal(principal);

        if (user.getNewPassword() != null) {

            user.setPassword(passwordEncoder.encode(user.getNewPassword()));

        }

        user.setOrganization(principalUser.getOrganization());
        user.setStatus(Status.ACTIVE);
//        user.getRoles().add(userRolesService.getByName(UserRole.ROLE_USERS_READONLY));
        user.setToken(UUID.randomUUID());
        user.setConfirmEmailToken(UUID.randomUUID());
        user.setStampConfirmSent(LocalDateTime.now());

        return usersRepository.save(user);

    }

    public User setPassword(UUID id, String newPassword) {

        User user = getById(id);

        user.setPassword(passwordEncoder.encode(newPassword));

        return usersRepository.save(user);

    }

    public User updateUserByPrincipal(User user, Principal principal) {

        User getUser = getByPrincipal(principal);

//        if (user.getEmail() != null) {
//
//            getUser.setEmail(user.getEmail());
//
//        }

        if (user.getFirstname() != null) {

            getUser.setFirstname(user.getFirstname());

        }

        if (user.getLastname() != null) {

            getUser.setLastname(user.getLastname());

        }

        if (user.getNewPassword() != null) {

            getUser.setPassword(passwordEncoder.encode(user.getNewPassword()));

        }

        if (user.getFirstname() != null) {

            getUser.setFirstname(user.getFirstname());

        }

        if (user.getLastname() != null) {

            getUser.setLastname(user.getLastname());

        }

        return usersRepository.save(getUser);

    }

    public Optional<User> getPrincipalUser(Principal principal) {

        return usersRepository.getByEmail(principal.getName());

    }

    public User getByPrincipal(Principal principal) {

        Optional<User> optionalUser = getPrincipalUser(principal);

        if (optionalUser.isPresent()) {

            return optionalUser.get();

        } else {

            throw new PrincipalNotFoundException();

        }

    }

    public Optional<User> getByPrincipalOrganizationAndId(Principal principal, UUID id) {

        Optional<User> principalUser = getPrincipalUser(principal);

        if (principalUser.isPresent()) {

            return usersRepository.getByOrganizationAndId(principalUser.get().getOrganization(), id);

        }

        return Optional.empty();

    }

    public int deleteByPrincipalOrganizationAndId(Principal principal, UUID userId) {

        Optional<User> principalUser = getPrincipalUser(principal);

        if (principalUser.isPresent()) {

            return usersRepository.deleteByOrganizationAndId(principalUser.get().getOrganization(), userId);

        }

        return 0;

    }

    public Optional<User> getByEmailAndPassword(String email, String password) {

        User user = getByEmail(email);

        if (passwordEncoder.matches(password, user.getPassword())) {

            return Optional.of(user);

        } else {

            return Optional.empty();

        }

    }

    public User create(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return usersRepository.save(user);

    }

    public Optional<User> create(User user, UUID organizationId) {

        Organization organization = organizationsService.getById(organizationId);

        user.setStatus(Status.ACTIVE);
        user.setOrganization(organization);
        user.setPassword(passwordEncoder.encode(user.getNewPassword()));

        return Optional.of(usersRepository.save(user));

    }

    public Optional<User> changePassword(UserPassword userPassword, Principal principal) {

        Optional<User> optionalUser = getPrincipalUser(principal);

        if (optionalUser.isPresent()) {

            optionalUser.get().setPassword(passwordEncoder.encode(userPassword.getPassword()));

            return Optional.of(usersRepository.save(optionalUser.get()));

        }

        return Optional.empty();

    }

    private boolean checkForPrivilege(User user, String name) {

        return user.getPrivileges().contains(name);

    }

    public boolean mustHavePrivilege(UUID uuid, String name) {

        if (checkForPrivilege(getById(uuid), name)) {

            return true;

        } else {

            throw new PermissionDeniedException();

        }

    }

    public boolean mustHavePrivilege(Principal principal, String name) {

        if (checkForPrivilege(getByPrincipal(principal), name)) {

            return true;

        } else {

            throw new PermissionDeniedException();

        }

    }

}
