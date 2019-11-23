package springblack.identity.roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springblack.common.Patterns;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/users/roles")
public class UserRolesController {

    private final UserRolesService userRolesService;

    @Autowired
    public UserRolesController(final UserRolesService userRolesService) {

        this.userRolesService = userRolesService;

    }

    @GetMapping
    public ResponseEntity<Page<UserRole>> getByPrincipalOrganization(Principal principal, Pageable pageable) {

        return ResponseEntity.ok(userRolesService.getByPrincipalOrganization(principal, pageable));

    }

    @GetMapping(Patterns.UUIDv4)
    public ResponseEntity<UserRole> getByIdAndPrincipalOrganization(@PathVariable("uuid") UUID id, Principal principal) {

        return ResponseEntity.ok(userRolesService.getByIdAndPrincipalOrganization(id, principal));

    }

    @PutMapping(Patterns.UUIDv4)
    public ResponseEntity<UserRole> deleteByIdAndOrganization(@PathVariable("uuid") UUID id, @RequestBody UserRole userRole, Principal principal) {

        return ResponseEntity.ok(userRolesService.updateByIdAndPrincipal(id, userRole, principal));

    }

    @DeleteMapping(Patterns.UUIDv4)
    public ResponseEntity<Boolean> deleteByIdAndOrganization(@PathVariable("uuid") UUID id, Principal principal) {

        return ResponseEntity.ok(userRolesService.deleteByIdAndOrganization(id, principal));

    }

    @PostMapping
    public ResponseEntity<UserRole> create(@RequestBody UserRole userRole, Principal principal) {

        return ResponseEntity.ok(userRolesService.createForPrincipalOrganization(userRole, principal));

    }

    @PostMapping("/{roleId}/privileges/{privilegeId}")
    public ResponseEntity<UserRole> addPrivilege(@PathVariable("roleId") UUID roleId, @PathVariable("privilegeId") UUID privilegeId, Principal principal) {

        return ResponseEntity.ok(userRolesService.addPrivilege(roleId, privilegeId, principal));

    }

    @DeleteMapping("/{roleId}/privileges/{privilegeId}")
    public ResponseEntity<Boolean> deletePrivilege(@PathVariable("roleId") UUID roleId, @PathVariable("privilegeId") UUID privilegeId, Principal principal) {

        userRolesService.deletePrivilege(roleId, privilegeId, principal);

        return ResponseEntity.ok(true);

    }

}
