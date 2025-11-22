package LapTrinhMang.WeChat.Dto.Response;

import LapTrinhMang.WeChat.Entity.RoomMember;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMemberResponse {
    private String id;
    private String roomId;
    private String userId;
    private String userName;
    private String avatar;
    private String role;
    private LocalDateTime joinedAt;

    public static RoomMemberResponse convert(RoomMember member){
        return RoomMemberResponse.builder()
                .id(member.getId())
                .roomId(member.getRoom().getId())
                .userId(member.getUser().getId())
                .userName(member.getUser().getUserName())
                .avatar(member.getUser().getAvatar())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
