package springblack.identity.organizations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springblack.common.Patterns;
import springblack.identity.users.UsersRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/organizations")
public class OrganizationsController {

    private OrganizationsRepository repository;
    private OrganizationsService    organizationsService;
    private UsersRepository         usersRepository;

    @Autowired
    public OrganizationsController(final OrganizationsRepository repository,
                                   final OrganizationsService organizationsService,
                                   final UsersRepository usersRepository) {

        this.repository = repository;
        this.organizationsService = organizationsService;
        this.usersRepository = usersRepository;

    }

    @GetMapping()
    public Page<Organization> getAllOrganizations(Pageable pageable) {

        return repository.findAll(pageable);

    }

    @GetMapping(Patterns.UUIDv4)
    public ResponseEntity<Organization> getById(@PathVariable("id") UUID id) {

        try {

            Optional<Organization> Organization = repository.findById(id);

            return Organization.map(organization -> new ResponseEntity<>(organization, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(new Organization(), HttpStatus.NOT_FOUND));

        } catch (EmptyResultDataAccessException ex) {

            return new ResponseEntity<>(new Organization(), HttpStatus.NOT_FOUND);

        }

    }

    @PostMapping
    public ResponseEntity<Organization> create(@RequestBody Organization entity) {

        return new ResponseEntity<>(repository.save(entity), HttpStatus.OK);

    }

    @PostMapping("/my")
    public ResponseEntity<Organization> createByPrincipal(@RequestBody Organization organization, Principal principal) {

        return new ResponseEntity<>(organizationsService.createByPrincipal(organization, principal).get(), HttpStatus.OK);

    }

    @PutMapping(Patterns.UUIDv4)
    public ResponseEntity<Organization> update(@PathVariable("id") UUID id, @RequestBody Organization entity) {

        entity.setId(id);

        return new ResponseEntity<>(repository.save(entity), HttpStatus.OK);

    }

    @DeleteMapping(Patterns.UUIDv4)
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {

        try {

            repository.deleteById(id);

            return new ResponseEntity<>(null, HttpStatus.OK);

        } catch (EmptyResultDataAccessException ex) {

            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        }

    }

    // TODO: port
//    @GetMapping(path = "/{id}/users")
//    public Page<User> getUsersByOrganizationId(@PathVariable("id") UUID organizationId, Pageable pageable) {
//
//        Page<User> page = this.usersRepository.getByOrganization_id(organizationId, pageable);
//
//        return page;
//
//    }

    @GetMapping(path = "/my/list")
    public ResponseEntity<List<Organization>> getMyOrganizationsByPrincipal(Principal principal, Pageable pageable) {

        return new ResponseEntity<>(organizationsService.getAllByPrincipalOrganization(principal, pageable), HttpStatus.OK);

    }

    @GetMapping(path = "/my/{id}")
    public ResponseEntity<Organization> getByIdAndPrincipal(@PathVariable("id") UUID id, Principal principal) {

        return ResponseEntity.ok(organizationsService.getByIdAndPrincipal(id, principal));

    }

    @PutMapping(path = "/my/{id}")
    public ResponseEntity<Organization> updateByIdAndPrincipal(@PathVariable("id") UUID id, @RequestBody Organization organization, Principal principal) {

        return ResponseEntity.ok(organizationsService.updateByIdAndPrincipal(id, organization, principal));

    }

    @DeleteMapping(path = "/my/{id}")
    public ResponseEntity<?> deleteByIdAndPrincipal(@PathVariable("id") UUID id, Principal principal) {

        organizationsService.deleteByIdAndPrincipal(id, principal);

        return new ResponseEntity<>(HttpStatus.OK);

    }

}
