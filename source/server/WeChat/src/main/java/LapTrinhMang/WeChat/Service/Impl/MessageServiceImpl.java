package LapTrinhMang.WeChat.Service.Impl;

import LapTrinhMang.WeChat.Dto.Request.MessageRequest;
import LapTrinhMang.WeChat.Dto.Response.MessageResponse;
import LapTrinhMang.WeChat.Entity.Message;
import LapTrinhMang.WeChat.Entity.Room;
import LapTrinhMang.WeChat.Entity.User;
import LapTrinhMang.WeChat.Enums.MessageState;
import LapTrinhMang.WeChat.Exception.BadRequestException;
import LapTrinhMang.WeChat.Exception.NotFoundException;
import LapTrinhMang.WeChat.Repository.MessageRepository;
import LapTrinhMang.WeChat.Repository.RoomMemberRepository;
import LapTrinhMang.WeChat.Repository.RoomRepository;
import LapTrinhMang.WeChat.Repository.UserRepository;
import LapTrinhMang.WeChat.Service.CentrifugoService;
import LapTrinhMang.WeChat.Service.MessageService;
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
public class MessageServiceImpl implements MessageService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final CentrifugoService centrifugoService;
    @Override
    public List<MessageResponse> listMessageByRoom(String roomId) {
        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(()->new NotFoundException("User not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("User not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()->new NotFoundException("User not found"));
        boolean roomExist = roomMemberRepository.existsByRoomAndUser(room,user);
        if(roomExist){
            List<MessageResponse> listMessage = messageRepository.listMessageByRoom(roomId)
                    .stream().map(MessageResponse::convert).toList();
            return listMessage;
        }else{
            throw new BadRequestException("User không phải là thành viên phòng này");
        }
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(MessageRequest request) {
        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(()->new NotFoundException("User not found"));
        User sender = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("User not found"));
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(()->new NotFoundException("Room not found"));
        Message message = Message.builder()
                .sender(sender)
                .room(room)
                .type(request.getMessageType())
                .state(MessageState.SENT)
                .content(request.getContent())
                .linkUrl(request.getLinkUrl())
                .fileName(request.getFileName())
                .build();

        message.setCreatedAt(LocalDateTime.now());
        messageRepository.save(message);

        room.incrementVersion();
        room.setLastMessage(message);
        room.setBumpedAt(LocalDateTime.now());
        roomRepository.save(room);

        //Dành cho những ai đang mở phòng này
        String roomChannel = "room#"+room.getId();
        centrifugoService.broadcast(List.of(roomChannel),MessageResponse.convert(message),"room_message_"+message.getId());

        //Thông báo đến tất cả thành viên
        List<String> userIds = roomMemberRepository.findUserIdsByRoomId(request.getRoomId());
        List<String> channels= userIds.stream()
                .map(u->"personal#"+u)
                .toList();

        Map<String,Object> data = Map.of(
                "type","message_added",
                "roomId",room.getId(),
                "message",MessageResponse.convert(message)
        );
        centrifugoService.broadcast(channels,data,"message_"+message.getId());
        return MessageResponse.convert(message);
    }
}
