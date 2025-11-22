package LapTrinhMang.WeChat.Repository;

import LapTrinhMang.WeChat.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,String> {
    @Query("SELECT m FROM Message m WHERE m.room.id=:roomId ORDER BY m.createdAt ASC")
    List<Message> listMessageByRoom(@Param("roomId") String roomId);
}
