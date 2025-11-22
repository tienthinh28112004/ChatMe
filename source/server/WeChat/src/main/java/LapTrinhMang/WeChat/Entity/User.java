package LapTrinhMang.WeChat.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "user_name"),
    @UniqueConstraint(columnNames = "email")}
)
public class User extends AbstractEntity<String> {

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "avatar")
    private String avatar;

    // Các message mà user gửi trong mọi room
    @OneToMany(mappedBy = "sender")
    private List<Message> messages;

    // Membership trong các room
    @OneToMany(mappedBy = "user")
    private List<RoomMember> roomMemberships;
}
