package LapTrinhMang.WeChat.Repository;

import LapTrinhMang.WeChat.Entity.Room;
import LapTrinhMang.WeChat.Enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,String> {
    //Danh sách đoạnc chat của user hiện tại
    @Query("SELECT DISTINCT r FROM Room r JOIN r.memberships m WHERE m.user.id=:userId ORDER BY r.bumpedAt desc")
    List<Room> findAllByMember(@Param("userId") String userId);

    //Danh sách đoạn chat phù hợp với keyword
    @Query("SELECT r FROM Room r WHERE lower(r.name) LIKE lower(concat('%',:keyword,'%'))")
    List<Room> findAllByKeyword(@Param("keyword") String keyword);
    @Query("SELECT r FROM Room r JOIN r.memberships mb1 JOIN r.memberships mb2 WHERE r.roomType =:roomType AND mb1.user.id=:userId AND mb2.user.id= :partnerId")
    Optional<Room> findRoomBetweenUsers(@Param("userId") String userId, @Param("partnerId") String partnerId, @Param("roomType")RoomType roomType);
}
