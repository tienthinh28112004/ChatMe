package LapTrinhMang.WeChat.Dto.Response;

import LapTrinhMang.WeChat.Entity.Message;
import LapTrinhMang.WeChat.Enums.MessageState;
import LapTrinhMang.WeChat.Enums.MessageType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import LapTrinhMang.WeChat.Entity.Room;
import LapTrinhMang.WeChat.Entity.User;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class MessageResponse {
    private String id;
    private String roomId;
    private Long roomVersion;
    private String senderId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private MessageType messageType;
    private MessageState messageState;
    private String url;
    private String fileName;
    private String lastMessageTime;

    public static MessageResponse convert(Message message) {
        log.info(message.getCreatedAt()+"2");
        Room room = message.getRoom();
        User sender = message.getSender();

        return MessageResponse.builder()
                .id(message.getId())
                .roomId(room != null ? room.getId() : null)
                .roomVersion(room != null ? room.getVersion() : null)
                .senderId(sender != null ? sender.getId() : null)
                .senderName(sender != null ? sender.getUserName() : null)
                .senderAvatar(sender != null ? sender.getAvatar() : null)
                .content(message.getContent())
                .messageType(message.getType())
                .messageState(message.getState())
                .fileName(message.getFileName())
                .url(message.getLinkUrl())
                .lastMessageTime(formatTime(message.getCreatedAt()))
                .build();
    }
    private static String formatTime(LocalDateTime time) {
        if (time == null) {
            log.info("1234556");
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
