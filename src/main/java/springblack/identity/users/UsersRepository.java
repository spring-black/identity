package springblack.identity.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import springblack.identity.organizations.Organization;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends PagingAndSortingRepository<User, UUID> {

    Page<User> getByOrganization(Organization organization, Pageable pageable);

    Optional<User> getByUsername(String username);

//    @Transactional
//    @Query(value = "SELECT * FROM users u WHERE (u.firstname LIKE :term1 OR u.lastname LIKE :term2 OR u.email LIKE :term3) AND u.status = :status ORDER BY u.id DESC", nativeQuery = true)
//    Page<User> _search(@Param("term1") String term1, @Param("term2") String term2, @Param("term3") String term3, @Param("status") int status, Pageable pageable);
//
//    @Query(value = "SELECT COUNT(u.id) FROM users u WHERE u.status = :status", nativeQuery = true)
//    Integer _stats(@Param("status") int status);

    Optional<User> getByEmail(String email);

    Optional<User> getByEmailAndPassword(String email, String password);

    Optional<User> getByConfirmEmailToken(UUID confirmEmailToken);

    Optional<User> getByPasswordResetToken(String passwordResetToken);

    Optional<User> getByOrganizationAndId(Organization organization, UUID id);

    @Transactional
    @Modifying
    int deleteByOrganizationAndId(Organization organization, UUID id);

}
