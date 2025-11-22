package LapTrinhMang.WeChat.Dto.Request;

import LapTrinhMang.WeChat.Enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {
    private String content;
    private MessageType messageType;
    private String roomId;
    private String linkUrl;//dùng cho file
    private String fileName;
}
