package springblack.identity.roles;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springblack.identity.organizations.Organization;
import springblack.identity.privileges.UserPrivilege;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRolesRepository extends PagingAndSortingRepository<UserRole, UUID> {

    Optional<UserRole> getByName(String name);

    Page<UserRole> getByOrganizationsIn(Organization organization, Pageable pageable);

    Optional<UserRole> getByIdAndOrganizationsIn(UUID id, Organization organization);

    Optional<UserRole> getByIdAndOrganizationsInAndPrivilegesIn(UUID id, Organization organization, UserPrivilege privilege);

    Optional<UserRole> getByNameAndOrganizationsIn(String name, Organization organization);

    @Transactional
    @Modifying
    int deleteByIdAndOrganizationsIn(UUID id, Organization organization);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM users_roles_privileges_links WHERE role_id = :role_id AND privilege_id = :privilege_id", nativeQuery = true)
    void _deletePrivilege(@Param("role_id") UUID role_id, @Param("privilege_id") UUID privilege_id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO users_roles_privileges_links SET role_id = :role_id, privilege_id = :privilege_id", nativeQuery = true)
    void _addPrivilege(@Param("role_id") UUID role_id, @Param("privilege_id") UUID privilege_id);

}
