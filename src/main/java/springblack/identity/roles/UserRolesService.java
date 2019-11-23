package springblack.identity.roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import springblack.common.exceptions.ResourceNotFoundException;
import springblack.identity.privileges.UserPrivilege;
import springblack.identity.privileges.UserPrivilegesService;
import springblack.identity.users.User;
import springblack.identity.users.UsersService;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserRolesService {

    private final UserRolesRepository   userRolesRepository;
    private final UserPrivilegesService userPrivilegesService;
    private final UsersService          usersService;

    @Autowired
    public UserRolesService(final UserRolesRepository userRolesRepository,
                            @Lazy final UserPrivilegesService userPrivilegesService,
                            @Lazy final UsersService usersService) {

        this.userRolesRepository = userRolesRepository;
        this.userPrivilegesService = userPrivilegesService;
        this.usersService = usersService;

    }

    public UserRole create(UserRole role) {

        return userRolesRepository.save(role);

    }

    public UserRole createForPrincipalOrganization(UserRole role, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_ROLES_ORG_CREATE);

        role.setOrganizations(Collections.singletonList(usersService.getByPrincipal(principal).getOrganization()));

        return userRolesRepository.save(role);

    }

    public UserRole getByIdAndPrincipalOrganization(UUID id, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_ROLES_ORG_GET_BYID);

        Optional<UserRole> optionalUserRole = userRolesRepository.getByIdAndOrganizationsIn(id, usersService.getByPrincipal(principal).getOrganization());

        return optionalUserRole.orElseThrow(() -> new ResourceNotFoundException("could not locate role"));

    }

    public UserRole getByNameAndPrincipalOrganization(String name, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_ROLES_ORG_GET_BYNAME);

        Optional<UserRole> optionalUserRole = userRolesRepository.getByNameAndOrganizationsIn(name, usersService.getByPrincipal(principal).getOrganization());

        return optionalUserRole.orElseThrow(() -> new ResourceNotFoundException("could not locate role"));

    }

    public UserRole getByName(String name) {

        return userRolesRepository.getByName(name).orElseThrow(() -> new ResourceAccessException("could not locate role"));

    }

    public Page<UserRole> getByPrincipalOrganization(Principal principal, Pageable pageable) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_ROLES_ORG_SEARCH);

        return userRolesRepository.getByOrganizationsIn(usersService.getByPrincipal(principal).getOrganization(), pageable);

    }

    public UserRole updateByIdAndPrincipal(UUID id, UserRole userRole, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PRIVILEGES_UPDATE);

        UserRole role = getByIdAndPrincipalOrganization(id, principal);

        role.setName(userRole.getName());
        role.setFriendlyName(userRole.getFriendlyName());
        role.setDescription(userRole.getDescription());

        return userRolesRepository.save(role);

    }

    public boolean deleteByIdAndOrganization(UUID id, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PRIVILEGES_DELETE);

        return userRolesRepository.deleteByIdAndOrganizationsIn(id, usersService.getByPrincipal(principal).getOrganization()) > 0;

    }

    public User deleteRoleForUserByPrincipal(UUID roleId, UUID userId, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_MANAGE_ROLES_DELETE);

        User     user     = usersService.getByIdAndPrincipalOrganization(userId, principal);
        UserRole userRole = getByIdAndPrincipalOrganization(roleId, principal);

        user.getRoles().remove(userRole);

        return usersService.updateUserByPrincipal(user, principal);

    }

    public User addToUser(UUID roleId, UUID userId, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PRIVILEGES_ASSIGN);

        User     user     = usersService.getByIdAndPrincipalOrganization(userId, principal);
        UserRole userRole = getByIdAndPrincipalOrganization(roleId, principal);

        user.getRoles().add(userRole);

        return usersService.updateUserByPrincipal(user, principal);

    }

    public void deletePrivilege(UUID roleId, UUID privilegeId, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PRIVILEGES_ASSIGN);

        userRolesRepository._deletePrivilege(roleId, privilegeId);

    }

    public UserRole addPrivilege(UUID roleId, UUID privilegeId, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PRIVILEGES_ASSIGN);

        UserRole role = getByIdAndPrincipalOrganization(roleId, principal);

        role.getPrivileges().add(userPrivilegesService.getById(privilegeId));

        return userRolesRepository.save(role);

    }

}
