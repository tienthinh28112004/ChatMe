package LapTrinhMang.WeChat.Service.Impl;

import LapTrinhMang.WeChat.Dto.Response.RoomMemberResponse;
import LapTrinhMang.WeChat.Entity.Room;
import LapTrinhMang.WeChat.Entity.RoomMember;
import LapTrinhMang.WeChat.Entity.User;
import LapTrinhMang.WeChat.Exception.NotFoundException;
import LapTrinhMang.WeChat.Repository.RoomMemberRepository;
import LapTrinhMang.WeChat.Repository.RoomRepository;
import LapTrinhMang.WeChat.Repository.UserRepository;
import LapTrinhMang.WeChat.Service.CentrifugoService;
import LapTrinhMang.WeChat.Service.RoomMemberService;
import LapTrinhMang.WeChat.Utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomMemberServiceImpl implements RoomMemberService {
    private final CentrifugoService centrifugoService;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    @Override
    @Transactional
    public RoomMemberResponse joinRoom(String roomId) {
        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(()->new NotFoundException("User not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("User not found"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(()->new NotFoundException("Room not found"));
        RoomMember member = roomMemberRepository.findRoomMember(roomId, userId);
        if(member !=null) return RoomMemberResponse.convert(member);
        // Nếu chưa có -> tạo mới
        member = RoomMember.builder()
                .room(room)
                .user(user)
                .role("MEMBER") // joinedAt sẽ được tự tạo từ PrePersist
                .build();
        roomMemberRepository.save(member);

        room.incrementVersion();
        room.setBumpedAt(LocalDateTime.now());
        roomRepository.save(room);

        List<String> channels = roomMemberRepository.findUserIdsByRoomId(roomId)
                .stream().map(id->"personal#"+id)
                .toList();

        Map<String,Object> data = Map.of(
                "type","user_joined",
                "body",RoomMemberResponse.convert(member)
        );
        centrifugoService.broadcast(channels,data,"user_joined_"+member.getId());

        return RoomMemberResponse.convert(member);
    }

    @Override
    @Transactional
    public RoomMemberResponse leaveRoom(String roomId) {
        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(()-> new NotFoundException("User not found"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(()->new NotFoundException("Room not found"));

        RoomMember member = roomMemberRepository.findRoomMember(roomId,userId);
        RoomMemberResponse response = RoomMemberResponse.convert(member);

        roomMemberRepository.delete(member);//user rời khỏi phòng

        room.incrementVersion();
        room.setBumpedAt(LocalDateTime.now());
        roomRepository.save(room);

        List<String> channels = roomMemberRepository.findUserIdsByRoomId(roomId)
                .stream().map(id->"personal#"+id)
                .toList();

        Map<String,Object> data = Map.of(
                "type","user_leave",
                "body",response
        );
        centrifugoService.broadcast(channels,data,"user_leave_"+member.getId());
        return response;
    }
}
