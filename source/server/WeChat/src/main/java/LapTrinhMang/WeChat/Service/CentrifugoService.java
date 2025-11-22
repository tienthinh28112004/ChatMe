package LapTrinhMang.WeChat.Service;

import LapTrinhMang.WeChat.Dto.Response.RoomResponse;

import java.util.List;
import java.util.Map;

public interface CentrifugoService {
    String createConnectionToken();
    String createSubcriptionToken(String channel);
    void broadcast(List<String> channels, Object data, String idempotencyKey);
    boolean isUserOnline(String userId);
    int getRoomOnlineCount(String roomId);
    Map<String,Boolean> getUsersOnlineStatus(List<String> userIds);
    void userStatusChanged(boolean online);
    void broadcastRoomCreated(List<String> userIds, RoomResponse room);
}
