package LapTrinhMang.WeChat.Entity;

import LapTrinhMang.WeChat.Enums.RoomType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rooms")
public class Room extends AbstractEntity<String> {

    @Column(name = "name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private RoomType roomType;

    // version phục vụ đồng bộ với Centrifugo, mỗi lần gửi message tăng 1
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    //thời điểm phòng Room bị đẩy lên đầu danh sách”
    @Column(name = "bumped_at", nullable = false)
    private LocalDateTime bumpedAt;

    @OneToOne
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @OneToMany(mappedBy = "room")
    private List<RoomMember> memberships;

    @OneToMany(mappedBy = "room")
    @OrderBy("createdAt ASC") //
    private List<Message> messages;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (bumpedAt == null) bumpedAt = now;
        if (version == null) version = 0L;
    }

    public Long incrementVersion() {
        this.version = this.version + 1;
        return this.version;
    }

}