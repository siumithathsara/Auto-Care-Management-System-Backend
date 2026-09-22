package ijse.lk.AutoCareManagement.repository;

import ijse.lk.AutoCareManagement.entity.User;
import ijse.lk.AutoCareManagement.enumeration.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndPassword(String username, String password);

    Optional<User> findByUsername(String username);

    Optional<User> findByUserCode(String userCode);

    Optional<User> findByUserCodeAndStatus(String userCode, UserStatus status);

    List<User> findAllByStatus(UserStatus status);

    Optional<User> findByUsernameAndStatus(String username, UserStatus status);

    List<User> findByUsernameContainingIgnoreCaseAndStatus(String username, UserStatus status);

    Boolean existsByUsername(String username);

    Boolean existsByUserCode(String userCode);

    Boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u")
    long getAllUserCount();

    @Query("SELECT u FROM User u WHERE (u.userCode = :identifier OR u.username = :identifier) AND u.status = 'ACTIVE'")
    Optional<User> findByUserCodeOrUsernameAndStatus(@Param("identifier") String identifier);

}
