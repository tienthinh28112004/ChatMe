package LapTrinhMang.WeChat.Controller;

import LapTrinhMang.WeChat.Dto.Request.MessageRequest;
import LapTrinhMang.WeChat.Dto.Response.ApiResponse;
import LapTrinhMang.WeChat.Dto.Response.MessageResponse;
import LapTrinhMang.WeChat.Service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;



import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/rooms/{roomId}/messages")
    public ApiResponse<List<MessageResponse>> listMessage(@PathVariable String roomId){
        return ApiResponse.<List<MessageResponse>>builder()
                .message("Danh sách tin nhắn của room")
                .result(messageService.listMessageByRoom(roomId))
                .build();
    }

    @PostMapping("/rooms/sendMessage")
    public ApiResponse<MessageResponse> sendMessage(@RequestBody MessageRequest request) throws JsonProcessingException{
        return ApiResponse.<MessageResponse>builder()
                .message("gửi tin nhắn đến centrifugo(TCP)")
                .result(messageService.sendMessage(request))
                .build();
    }

}
