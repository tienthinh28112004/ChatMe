package LapTrinhMang.WeChat.Controller;

import LapTrinhMang.WeChat.Dto.Response.ApiResponse;
import LapTrinhMang.WeChat.Dto.Response.UserResponse;
import LapTrinhMang.WeChat.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/getUserById/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId){
        return ApiResponse.<UserResponse>builder()
                .message("Lấy thông tin user qua id")
                .result(userService.getUserById(userId))
                .build();
    }
    @GetMapping("/getMyInfo")
    public ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder()
                .message("Lấy thông tin user đang đăng nhâp")
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping("/getUserByKeyword")
    public ApiResponse<List<UserResponse>> getUserByKeyword(@Param("keyword") String keyword){
        log.info(keyword);
        return ApiResponse.<List<UserResponse>>builder()
                .message("Tìm user by keyword")
                .result(userService.getUserByKeyword(keyword))
                .build();
    }
}
