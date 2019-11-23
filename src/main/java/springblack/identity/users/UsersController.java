package springblack.identity.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springblack.common.Patterns;
import springblack.common.RequestResult;
import springblack.common.Status;
import springblack.identity.privileges.UserPrivilege;
import springblack.identity.roles.UserRolesService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UsersRepository  repository;
    private final UserRolesService userRolesService;
    private final UsersService     usersService;

    @Autowired
    public UsersController(final UsersRepository repository,
                           final UserRolesService userRolesService,
                           final UsersService usersService) {

        this.repository = repository;
        this.userRolesService = userRolesService;
        this.usersService = usersService;

    }

    // TODO: port
    @GetMapping("/byorg")
    public ResponseEntity<?> getAll(Principal principal, Pageable pageable) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_SEARCH_BYORG);

        Optional<User> principalUser = usersService.getPrincipalUser(principal);

        if (principalUser.isPresent()) {

            return new ResponseEntity<>(repository.getByOrganization(principalUser.get().getOrganization(), pageable), HttpStatus.OK);

        } else {

            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        }

    }

    @GetMapping(Patterns.UUIDv4)
    public ResponseEntity<User> getById(@PathVariable("uuid") UUID id, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_GET_BYID);

        return ResponseEntity.ok(usersService.getByIdAndPrincipalOrganization(id, principal));

    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody UserLogin userLogin) {

        Optional<User> user = usersService.getByEmailAndPassword(userLogin.getEmail(),
                                                                 userLogin.getPassword());

        if (user.isPresent()) {

            usersService.mustHavePrivilege(user.get().getId(), UserPrivilege.PRIVILEGE_USERS_LOGIN);

            if (user.get().getStatus().equals(Status.ACTIVE)) {

                user.get().setStampLastLogin(LocalDateTime.now());

                repository.save(user.get());

                return new ResponseEntity<>(new RequestResult(RequestResult.RESULT_OK, UserLogin.getJWT(userLogin.getEmail(), UserLogin.ONE_DAY_MILLIS)), HttpStatus.OK);

            } else if (user.get().getStatus().equals(Status.PENDING)) {

                return new ResponseEntity<>(new RequestResult(RequestResult.RESULT_ERROR,
                                                              "Your email address has not been confirmed. Please check your email or contact us.",
                                                              Status.PENDING), HttpStatus.OK);

            } else {

                return new ResponseEntity<>(new RequestResult(RequestResult.RESULT_ERROR, "Invalid email address and/or password."), HttpStatus.OK);

            }

        } else {

            return new ResponseEntity<>(new RequestResult(RequestResult.RESULT_ERROR, "Invalid email address and/or password."), HttpStatus.OK);

        }

    }

    @DeleteMapping(Patterns.UUIDv4)
    public ResponseEntity<?> delete(@PathVariable("uuid") UUID id, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_DELETE);

        return new ResponseEntity<>(usersService.deleteByPrincipalOrganizationAndId(principal, id), HttpStatus.OK);

    }

    @GetMapping(path = "/my")
    public ResponseEntity<User> getCurrentUser(Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PROFILE_GET);

        return new ResponseEntity<>(repository.getByEmail(principal.getName()).get(), HttpStatus.OK);

    }

    @PutMapping(path = "/my")
    public ResponseEntity<User> update(@RequestBody User user, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PROFILE_UPDATE);

        return ResponseEntity.ok(usersService.updateUserByPrincipal(user, principal));

    }

    @PostMapping
    public ResponseEntity<User> createByPrincipal(@RequestBody User user, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_CREATE_BYPRINCIPAL);

        return ResponseEntity.ok(usersService.createByPrincipal(user, principal));

    }

    @PostMapping("/password")
    public ResponseEntity<User> changePassword(@RequestBody UserPassword userPassword, Principal principal) {

        usersService.mustHavePrivilege(principal, UserPrivilege.PRIVILEGE_USERS_PROFILE_CHANGEPASSWORD);

        Optional<User> optionalUser = usersService.changePassword(userPassword, principal);

        return optionalUser.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));

    }

    @PostMapping("/{userId:[0-9a-fxA-FX]{8}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{12}}/roles/{roleId:[0-9a-fxA-FX]{8}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{12}}")
    public ResponseEntity<User> rolesAddToUser(@PathVariable("userId") UUID userId, @PathVariable("roleId") UUID roleId, Principal principal) {

        return ResponseEntity.ok(userRolesService.addToUser(roleId, userId, principal));

    }

    @DeleteMapping("/{userId:[0-9a-fxA-FX]{8}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{12}}/roles/{roleId:[0-9a-fxA-FX]{8}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{12}}")
    public ResponseEntity<User> rolesGetByUserId(@PathVariable("userId") UUID userId, @PathVariable("roleId") UUID roleId, Principal principal) {

        return ResponseEntity.ok(userRolesService.deleteRoleForUserByPrincipal(roleId, userId, principal));

    }

    @PostMapping("/confirm/{confirmToken:[0-9a-fxA-FX]{8}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{12}}")
    public ResponseEntity<Boolean> confirmByToken(@PathVariable("confirmToken") UUID confirmToken, @RequestBody UserPassword userPassword) {

        return ResponseEntity.ok(usersService.confirmByToken(confirmToken, userPassword));

    }

    @PostMapping("/{id:[0-9a-fxA-FX]{8}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{4}-[0-9a-fxA-FX]{12}}/confirm/resend")
    public ResponseEntity<Boolean> confirmByToken(@PathVariable("id") UUID id, Principal principal) {

        usersService.confirmResend(id, principal);

        return ResponseEntity.ok(true);

    }

}
