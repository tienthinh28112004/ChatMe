package LapTrinhMang.WeChat.Dto.Response;

import LapTrinhMang.WeChat.Entity.Message;
import LapTrinhMang.WeChat.Entity.Room;
import LapTrinhMang.WeChat.Enums.MessageType;
import LapTrinhMang.WeChat.Enums.RoomType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {

    private String id;
    private String name;
    private RoomType roomType;
    private Long version;
    private LocalDateTime bumpedAt;   // thời điểm hoạt động gần nhất(send message,join,leave)
    private String lastMessage;
    private String lastMessageTime;
    private long unreadCount;           // tin nhắn chưa đọc
    private boolean online;
    private long onlineCount; //số người đang online(group)
    private String avatarUrl;
    public static RoomResponse convert(Room room, long unreadCount, boolean online, long onlineCount, String avatarUrl) {

        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .roomType(room.getRoomType())
                .version(room.getVersion())
                .bumpedAt(room.getBumpedAt())
                .lastMessage(room.getLastMessage()!=null?room.getLastMessage().getContent():"")
                .lastMessageTime(room.getLastMessage()!=null?formatTime(room.getLastMessage().getCreatedAt()): "")
                .unreadCount(unreadCount)
                .online(online)
                .onlineCount(onlineCount)
                .avatarUrl(avatarUrl)
                .build();
    }
    private static String formatTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        LocalDate msgDate = time.toLocalDate();
        LocalDate today = LocalDate.now();

        if (msgDate.isEqual(today)) return time.format(DateTimeFormatter.ofPattern("HH:mm"));


        if (msgDate.isEqual(today.minusDays(1))) return "Hôm qua";

        if (msgDate.getYear() == today.getYear()) return time.format(DateTimeFormatter.ofPattern("dd/MM"));
        return time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
