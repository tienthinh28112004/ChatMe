package LapTrinhMang.WeChat.Entity;

import LapTrinhMang.WeChat.Enums.MessageState;
import LapTrinhMang.WeChat.Enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "messages")
public class Message extends AbstractEntity<String> {

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType type;//FILE,TEXT

    @Enumerated(EnumType.STRING)
    @Column(name = "message_state")
    private MessageState state;//SENT,SEND

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "file_name")
    private String fileName;
}