package springblack.identity.privileges;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import springblack.common.exceptions.RecordAlreadyExistsException;
import springblack.common.exceptions.ResourceNotFoundException;
import springblack.identity.users.UsersService;

import java.security.Principal;
import java.util.UUID;

@Service
public class UserPrivilegesService {

    private final UserPrivilegesRepository userPrivilegesRepository;
    private final UsersService             usersService;

    @Autowired
    public UserPrivilegesService(final UserPrivilegesRepository userPrivilegesRepository,
                                 final UsersService usersService) {

        this.userPrivilegesRepository = userPrivilegesRepository;
        this.usersService = usersService;

    }

    public Page<UserPrivilege> getAll(Principal principal, Pageable pageable) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PRIVILEGES_SEARCH);

        return userPrivilegesRepository.findAll(pageable);

    }

    public UserPrivilege getByName(String name) {

        return userPrivilegesRepository.getByName(name).orElseThrow(() -> new ResourceNotFoundException("could not locate privilege"));

    }

    public UserPrivilege getById(UUID id) {

        return userPrivilegesRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("could not locate privilege"));

    }

    public UserPrivilege create(UserPrivilege privilege) {

        return userPrivilegesRepository.save(privilege);

    }

    public UserPrivilege createByPrincipal(UserPrivilege privilege, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PRIVILEGES_CREATE);

        try {

            getByName(privilege.getName());

            throw new RecordAlreadyExistsException("Privilege with this name already exists");

        } catch (ResourceNotFoundException e) {

            return userPrivilegesRepository.save(privilege);

        }

    }

    public void deleteById(UUID id, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PRIVILEGES_CREATE);

        userPrivilegesRepository.deleteById(id);

    }

    public UserPrivilege updateById(UUID id, UserPrivilege privilege, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PRIVILEGES_UPDATE);

        UserPrivilege userPrivilege = getById(id);

        userPrivilege.setFriendlyName(privilege.getFriendlyName());
        userPrivilege.setDescription(privilege.getDescription());

        return userPrivilegesRepository.save(userPrivilege);

    }

}
