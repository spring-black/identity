package springblack.identity.organizations.links;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import springblack.identity.organizations.Organization;

@Repository
public interface OrganizationLinksRepository extends PagingAndSortingRepository<OrganizationLink, Long> {

    int deleteByChildAndParent(Organization child, Organization parent);

}
