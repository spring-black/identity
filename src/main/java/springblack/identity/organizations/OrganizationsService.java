package springblack.identity.organizations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import springblack.common.exceptions.ResourceNotFoundException;
import springblack.identity.organizations.links.OrganizationLinksService;
import springblack.identity.users.User;
import springblack.identity.users.UsersService;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrganizationsService {

    private OrganizationsRepository  organizationsRepository;
    private OrganizationLinksService organizationLinksService;

    private UsersService usersService;

    @Autowired
    public OrganizationsService(final OrganizationsRepository organizationsRepository,
                                final OrganizationLinksService organizationLinksService,
                                final UsersService usersService) {

        this.organizationsRepository = organizationsRepository;
        this.organizationLinksService = organizationLinksService;
        this.usersService = usersService;

    }

    public Organization getById(UUID id) {

        Optional<Organization> optionalOrganization = organizationsRepository.findById(id);

        if (optionalOrganization.isPresent()) {

            return optionalOrganization.get();

        } else {

            throw new ResourceNotFoundException("organization could not be located");

        }

    }

    public Organization getByName(String name) {

        Optional<Organization> optionalOrganization = organizationsRepository.getByName(name);

        if (optionalOrganization.isPresent()) {

            return optionalOrganization.get();

        } else {

            throw new ResourceNotFoundException("organization could not be located");
        }

    }

    public List<Organization> getAllByPrincipalOrganization(Principal principal, Pageable pageable) {

        Optional<User> principalUser = usersService.getPrincipalUser(principal);

        if (principalUser.isPresent()) {

            return organizationsRepository._getByParentId(principalUser.get().getOrganization().getId());

        }

        return new ArrayList<>(0);

    }

    public Organization create(Organization organization) {

        Organization createdOrganization = organizationsRepository.save(organization);

        organizationLinksService.createLink(createdOrganization, createdOrganization);

        return createdOrganization;

    }

    public Optional<Organization> createByPrincipal(Organization organization, Principal principal) {

        Optional<User> principalUser = usersService.getPrincipalUser(principal);

        if (principalUser.isPresent()) {

            Organization createdOrganization = organizationsRepository.save(organization);

            organizationLinksService.createLink(principalUser.get().getOrganization(), createdOrganization);

            return Optional.of(createdOrganization);

        }

        return Optional.empty();

    }

    @Transactional
    public Organization getByIdAndPrincipal(UUID id, Principal principal) {

        Optional<Organization> optionalOrganization = organizationsRepository._getByChildIdAndParentId(id, usersService.getByPrincipal(principal).getOrganization().getId());

        if (optionalOrganization.isPresent()) {

            return optionalOrganization.get();

        } else {

            throw new ResourceNotFoundException("organization could not be located.");

        }

    }

    @Transactional
    public Organization updateByIdAndPrincipal(UUID id, Organization organization, Principal principal) {

        Organization getOrganization = getByIdAndPrincipal(id, principal);

        getOrganization.setName(organization.getName());
        getOrganization.setDescription(organization.getDescription());
        getOrganization.setStatus(organization.getStatus());

        return organizationsRepository.save(getOrganization);

    }

    public void deleteByIdAndPrincipal(UUID id, Principal principal) {

        Organization getOrganization = getByIdAndPrincipal(id, principal);

        organizationLinksService.deleteByChildAndParent(getOrganization, usersService.getByPrincipal(principal).getOrganization());

        organizationsRepository.deleteById(id);

    }

}
