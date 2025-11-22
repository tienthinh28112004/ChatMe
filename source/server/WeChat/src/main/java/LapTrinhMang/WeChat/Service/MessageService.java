package LapTrinhMang.WeChat.Service;

import LapTrinhMang.WeChat.Dto.Request.MessageRequest;
import LapTrinhMang.WeChat.Dto.Response.MessageResponse;

import java.util.List;

public interface MessageService {
    List<MessageResponse> listMessageByRoom(String roomId);
    MessageResponse sendMessage(MessageRequest request);
}
