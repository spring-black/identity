package springblack.identity.privileges;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPrivilegesRepository extends PagingAndSortingRepository<UserPrivilege, UUID> {

    Optional<UserPrivilege> getByName(String name);

}
