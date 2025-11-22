package LapTrinhMang.WeChat.Service;

import LapTrinhMang.WeChat.Dto.Request.CreateRoomRequest;
import LapTrinhMang.WeChat.Dto.Response.MessageResponse;
import LapTrinhMang.WeChat.Dto.Response.RoomResponse;

import java.util.List;

public interface RoomService {
    List<RoomResponse> listRoomByUser(String keyword);
    RoomResponse createRoom(CreateRoomRequest request);
}
