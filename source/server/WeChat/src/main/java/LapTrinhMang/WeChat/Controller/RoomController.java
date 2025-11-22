package LapTrinhMang.WeChat.Controller;

import LapTrinhMang.WeChat.Dto.Request.CreateRoomRequest;
import LapTrinhMang.WeChat.Dto.Response.ApiResponse;
import LapTrinhMang.WeChat.Dto.Response.RoomResponse;
import LapTrinhMang.WeChat.Service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
@Validated
public class RoomController {
    private final RoomService roomService;
    @GetMapping("/listRoom")
    public ApiResponse<List<RoomResponse>> listRoomByUser(@Param("keyword") String keyword){
        return ApiResponse.<List<RoomResponse>>builder()
                .message("Danh sách các phòng của tài khoản hiện tại")
                .result(roomService.listRoomByUser(keyword))
                .build();
    }

    @PostMapping("/addRoom")
    public ApiResponse<RoomResponse> createRoom(@RequestBody CreateRoomRequest request){
        return ApiResponse.<RoomResponse>builder()
                .message("Tạo room mới")
                .result(roomService.createRoom(request))
                .build();
    }
}
