package springblack.identity.privileges;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springblack.common.Patterns;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/users/privileges")
public class UserPrivilegesController {

    private final UserPrivilegesService userPrivilegesService;

    @Autowired
    public UserPrivilegesController(final UserPrivilegesService userPrivilegesService) {

        this.userPrivilegesService = userPrivilegesService;

    }

    @GetMapping
    public ResponseEntity<Page<UserPrivilege>> getAllByPrincipal(Principal principal, Pageable pageable) {

        return new ResponseEntity<>(userPrivilegesService.getAll(principal, pageable), HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<UserPrivilege> create(@RequestBody UserPrivilege userPrivilege, Principal principal) {

        return ResponseEntity.ok(userPrivilegesService.createByPrincipal(userPrivilege, principal));

    }

    @DeleteMapping(Patterns.UUIDv4)
    public ResponseEntity<Boolean> deleteById(@PathVariable("uuid") UUID id, Principal principal) {

        userPrivilegesService.deleteById(id, principal);

        return ResponseEntity.ok(true);

    }

}
