package LapTrinhMang.WeChat.Repository;

import LapTrinhMang.WeChat.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u\n" + " WHERE u.id <> :userId\n" + " AND LOWER(u.userName) LIKE LOWER(CONCAT('%', CONCAT(:keyword, '%')))")
    List<User> findUserByKeyword(@Param("keyword") String keyword,@Param("userId") String userId);
}
