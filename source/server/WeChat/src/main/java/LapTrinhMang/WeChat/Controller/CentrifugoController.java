package LapTrinhMang.WeChat.Controller;

import LapTrinhMang.WeChat.Dto.Response.ApiResponse;
import LapTrinhMang.WeChat.Service.CentrifugoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centrifugo")
@Slf4j
@RequiredArgsConstructor
public class CentrifugoController {

    private final CentrifugoService centrifugoService;
    @GetMapping("/connectionToken")
    public ApiResponse<String> connectionToken(){
        return ApiResponse.<String>builder()
                .message("Token dùng để kết nối với centrifugo")
                .result(centrifugoService.createConnectionToken())
                .build();
    }

    @GetMapping("/subcriptionToken")
    public ApiResponse<String> subcriptionToken(@Param("channels") String channels){
        log.info(channels+"kênh chính");
        return ApiResponse.<String>builder()
                .message("Token dùng để subcription vào kênh private riêng")
                .result(centrifugoService.createSubcriptionToken(channels))
                .build();
    }
    @GetMapping("/isUserOnline/{userId}")
    public ApiResponse<Boolean> isUserOnline(@PathVariable("userId") String userId){
        return ApiResponse.<Boolean>builder()
                .message("Kiểm tra xem userId này có online không")
                .result(centrifugoService.isUserOnline(userId))
                .build();
    }

    @GetMapping("/getRoomOnlineCount/{roomId}")
    public ApiResponse<Integer> getRoomOnlineCount(@PathVariable("roomId") String roomId){
        return ApiResponse.<Integer>builder()
                .message("Phòng này có bao nhiêu thiết bị đang online")
                .result(centrifugoService.getRoomOnlineCount(roomId))
                .build();
    }
    @PostMapping("/notifyOnline")
    public ApiResponse<String> notifyOnline(){
        centrifugoService.userStatusChanged(true);
        return ApiResponse.<String>builder()
                .message("Cập nhật trạng thái online của user này lên toàn hệ thống")
                .result("Cập nhật onlien thành công")
                .build();
    }
    @PostMapping("/notifyOffline")
    public ApiResponse<String> notifyOffline(){
        centrifugoService.userStatusChanged(false);
        return ApiResponse.<String>builder()
                .message("Cập nhật trạng thái offline của user này lên toàn hệ thống")
                .result("Cập nhật offline thành công")
                .build();
    }
}
