package LapTrinhMang.WeChat.Repository;

import LapTrinhMang.WeChat.Entity.Room;
import LapTrinhMang.WeChat.Entity.RoomMember;
import LapTrinhMang.WeChat.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember,String> {
    boolean existsByRoomAndUser(Room room, User user);
    @Query("SELECT rm.user.id FROM RoomMember rm WHERE rm.room.id=:roomId")
    List<String> findUserIdsByRoomId(@Param("roomId") String roomId);

    @Query("SELECT DISTINCT rm.room.id FROM RoomMember rm WHERE rm.user.id = :userId")
    List<String> findRoomIdsByUser(@Param("userId") String userId);

    @Query("SELECT rm FROM RoomMember rm WHERE rm.room.id=:roomId AND rm.user.id=:userId")
    RoomMember findRoomMember(@Param("roomId") String roomId,@Param("userId") String userId);

    //Lấy ra danh sách tất cả userId từng là thành viên cùng phòng với userId
    @Query("SELECT DISTINCT rm2.user.id FROM RoomMember rm1 JOIN RoomMember rm2 " +
            "ON rm1.room.id = rm2.room.id WHERE rm1.user.id = :userId AND rm2.user.id <>:userId")
    List<String> findIdsByUserInSameRoom(@Param("userId") String userId);
}
