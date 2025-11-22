package LapTrinhMang.WeChat.Dto.Response;

import LapTrinhMang.WeChat.Entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String userName;
    private String email;
    private LocalDateTime lastSeen;
    private boolean isOnline;
    private String avatarUrl;

    public static UserResponse convert(User user, boolean isOnline) {

        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .lastSeen(user.getLastSeen())
                .isOnline(isOnline)           // dùng boolean isOnline = presenceService.isUserOnline(user.getId()); và thao tác ở service
                .avatarUrl(user.getAvatar())
                .build();
    }
}