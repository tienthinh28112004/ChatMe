package LapTrinhMang.WeChat.Service.Impl;

import LapTrinhMang.WeChat.Dto.Request.CreateRoomRequest;
import LapTrinhMang.WeChat.Dto.Response.MessageResponse;
import LapTrinhMang.WeChat.Dto.Response.RoomResponse;
import LapTrinhMang.WeChat.Entity.Message;
import LapTrinhMang.WeChat.Entity.Room;
import LapTrinhMang.WeChat.Entity.RoomMember;
import LapTrinhMang.WeChat.Entity.User;
import LapTrinhMang.WeChat.Enums.MessageType;
import LapTrinhMang.WeChat.Enums.RoomType;
import LapTrinhMang.WeChat.Exception.BadRequestException;
import LapTrinhMang.WeChat.Exception.NotFoundException;
import LapTrinhMang.WeChat.Repository.RoomMemberRepository;
import LapTrinhMang.WeChat.Repository.RoomRepository;
import LapTrinhMang.WeChat.Repository.UserRepository;
import LapTrinhMang.WeChat.Service.CentrifugoService;
import LapTrinhMang.WeChat.Service.RoomService;
import LapTrinhMang.WeChat.Utils.SecurityUtils;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final CentrifugoService centrifugoService;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;
    @Override
    public List<RoomResponse> listRoomByUser(String keyword) {
        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Room> roomList;

        if (keyword != null && !keyword.isEmpty()) {
            roomList = roomRepository.findAllByKeyword(keyword);
        } else {
            roomList = roomRepository.findAllByMember(userId);
        }

        if (roomList.isEmpty()) return List.of();

        List<RoomResponse> result = new ArrayList<>();

        for (Room room : roomList) {
            boolean online = false;
            long onlineCount = 0;
            if (room.getRoomType() == RoomType.DIRECT) {

                // Tìm user còn lại
                String peerId = room.getMemberships().stream()
                        .map(m -> m.getUser().getId())
                        .filter(id -> !id.equals(userId))
                        .findFirst()
                        .orElse(null);

                if (peerId != null) {
                    log.info("{peerId} {name}");
                    // Kiểm tra online bằng user_status
                    online = centrifugoService.isUserOnline(peerId);
                    log.info(online+""+onlineCount+""+peerId);
                }
            }
            else if (room.getRoomType() == RoomType.GROUP) {

                List<String> memberIds = room.getMemberships().stream()
                        .map(m -> m.getUser().getId())
                        .filter(id -> !id.equals(userId))
                        .toList();

                if (!memberIds.isEmpty()) {
                    Map<String, Boolean> status =
                            centrifugoService.getUsersOnlineStatus(memberIds);

                    onlineCount = status.values().stream()
                            .filter(Boolean::booleanValue)
                            .count();

                    online = onlineCount > 0;
                    log.info(online+""+onlineCount+"group");
                }
            }

            result.add(RoomResponse.convert(room, 0, online, onlineCount,
                    "https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg"
            ));
        }

        return result;
    }


    @Override
    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        List<String> ids = request.getIds();
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("Danh sách user không được để trống");
        }

        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(() -> new NotFoundException("User not found"));

        RoomType type = (ids.size() > 1) ? RoomType.GROUP : RoomType.DIRECT;
        if (type == RoomType.DIRECT) {
            String partnerId = ids.get(0);
            Optional<Room> existingRoom =
                    roomRepository.findRoomBetweenUsers(userId, partnerId, RoomType.DIRECT);

            if (existingRoom.isPresent()) {
                Room room = existingRoom.get();

                boolean online = centrifugoService.isUserOnline(partnerId);
                long onlineCount = online ? 1 : 0;
                return RoomResponse.convert(room, 0, online, onlineCount,
                        "https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg"
                );
            }
        }
        //Tạo rooms
        Room room = Room.builder()
                .bumpedAt(LocalDateTime.now())
                .version(0L)
                .roomType(type)
                .lastMessage(null)
                .build();
        roomRepository.save(room);

        List<String> allUserIds = new ArrayList<>();
        allUserIds.add(userId);
        allUserIds.addAll(ids);

        List<RoomMember> roomMemberList = new ArrayList<>();

        for (String uid : allUserIds) {
            User u = userRepository.findById(uid)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            RoomMember rm = RoomMember.builder()
                    .room(room)
                    .user(u)
                    .role("MEMBER")
                    .joinedAt(LocalDateTime.now())
                    .build();

            roomMemberRepository.save(rm);
            roomMemberList.add(rm);
        }

        room.setMemberships(roomMemberList);
        roomRepository.save(room);

        boolean online = false;
        long onlineCount = 0;
        if (type == RoomType.DIRECT) {
            String peerId = ids.get(0);
            online = centrifugoService.isUserOnline(peerId);
            onlineCount = online ? 1 : 0;

        } else {
            List<String> otherIds = allUserIds.stream()
                    .filter(id -> !id.equals(userId))
                    .toList();
            if (!otherIds.isEmpty()) {
                Map<String, Boolean> status = centrifugoService.getUsersOnlineStatus(otherIds);
                onlineCount = status.values().stream().filter(Boolean::booleanValue).count();
                online = onlineCount > 0;
            }
        }

        RoomResponse response = RoomResponse.convert(room, 0, online, onlineCount,
                "https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg"
        );

        centrifugoService.broadcastRoomCreated(allUserIds, response);
        return response;
    }
}
