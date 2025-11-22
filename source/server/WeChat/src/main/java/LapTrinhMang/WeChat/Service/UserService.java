package LapTrinhMang.WeChat.Service;

import LapTrinhMang.WeChat.Dto.Response.UserResponse;

import java.util.List;

public interface UserService{
    List<UserResponse> getUserByKeyword(String keyword);
    UserResponse getMyInfo();
    UserResponse getUserById(String userId);
}
