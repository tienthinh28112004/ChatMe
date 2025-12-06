package LapTrinhMang.WeChat.Service.Impl;

import LapTrinhMang.WeChat.Dto.Request.MessageRequest;
import LapTrinhMang.WeChat.Dto.Response.MessageResponse;
import LapTrinhMang.WeChat.Entity.ChatCDC;
import LapTrinhMang.WeChat.Entity.Message;
import LapTrinhMang.WeChat.Entity.Room;
import LapTrinhMang.WeChat.Entity.User;
import LapTrinhMang.WeChat.Enums.MessageState;
import LapTrinhMang.WeChat.Exception.BadRequestException;
import LapTrinhMang.WeChat.Exception.NotFoundException;
import LapTrinhMang.WeChat.Repository.*;
import LapTrinhMang.WeChat.Service.CentrifugoService;
import LapTrinhMang.WeChat.Service.MessageService;
import LapTrinhMang.WeChat.Utils.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ChatCDCRepository chatCDCRepository;
    private final ObjectMapper objectMapper;
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
    public MessageResponse sendMessage(MessageRequest request) throws JsonProcessingException {
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

        MessageResponse messageResponse =MessageResponse.convert(message);
        //Dành cho những ai đang mở phòng này
        String roomChannel = "room#"+room.getId();
//        centrifugoService.broadcast(List.of(roomChannel),messageResponse,"room_message_"+message.getId());

        //Thông báo đến tất cả thành viên
        List<String> userIds = roomMemberRepository.findUserIdsByRoomId(request.getRoomId());
        List<String> channels= userIds.stream()
                .map(u->"personal#"+u)
                .toList();

        Map<String,Object> data = Map.of(
                "type","message_added",
                "roomId",room.getId(),
                "message",messageResponse
        );
//        centrifugoService.broadcast(channels,data,"message_"+message.getId());
        String partitionKey = room.getId();

        //Event cho room chanel
        Map<String,Object> roomPayload = Map.of(
                "method","broadcast",
                "channels",List.of(roomChannel),
                "data",messageResponse,
                "id","room_message_"+message.getId()
        );
        ChatCDC roomCDC = ChatCDC.builder()
                .method("broadcast")
                .partitionKey(partitionKey)
                .payload(objectMapper.writeValueAsString(roomPayload))
                .build();
        chatCDCRepository.save(roomCDC);
        //Event cho personals channels
        Map<String, Object> personalPayload = Map.of(
                "method", "broadcast",
                "channels", channels,
                "data", data,
                "id", "message_" + message.getId()
        );
        ChatCDC allMemberRoom = ChatCDC.builder()
                .method("broadcast")
                .payload(objectMapper.writeValueAsString(personalPayload))
                .partitionKey(partitionKey)
                .build();
        chatCDCRepository.save(allMemberRoom);

        return messageResponse;
    }
}
