package LapTrinhMang.WeChat.Controller;

import LapTrinhMang.WeChat.Dto.Response.ApiResponse;
import LapTrinhMang.WeChat.Dto.Response.RoomMemberResponse;
import LapTrinhMang.WeChat.Service.RoomMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RoomMemberController {
    private final RoomMemberService roomMemberService;

    @PostMapping("/rooms/join/{roomId}")
    public ApiResponse<RoomMemberResponse> joinRoom(@PathVariable("roomId") String roomId){
        return ApiResponse.<RoomMemberResponse>builder()
                .message("Xác nhận vào phòng")
                .result(roomMemberService.joinRoom(roomId))
                .build();
    }

    @PostMapping("/rooms/leave/{roomId}")
    public ApiResponse<RoomMemberResponse> leaveRoom(@PathVariable("roomId") String roomId){
        return ApiResponse.<RoomMemberResponse>builder()
                .message("Xác nhận thoát phòng")
                .result(roomMemberService.leaveRoom(roomId))
                .build();
    }
}
