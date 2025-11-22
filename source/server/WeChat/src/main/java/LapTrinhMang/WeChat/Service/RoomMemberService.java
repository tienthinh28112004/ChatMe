package LapTrinhMang.WeChat.Service;

import LapTrinhMang.WeChat.Dto.Response.RoomMemberResponse;

public interface RoomMemberService {
    RoomMemberResponse joinRoom(String roomId);
    RoomMemberResponse leaveRoom(String roomId);
}
