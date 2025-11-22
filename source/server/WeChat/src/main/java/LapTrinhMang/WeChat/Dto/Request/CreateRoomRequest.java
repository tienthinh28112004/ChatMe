package LapTrinhMang.WeChat.Dto.Request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomRequest {
    private List<String> ids;
}
