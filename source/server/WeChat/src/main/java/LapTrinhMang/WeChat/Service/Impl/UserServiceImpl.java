package LapTrinhMang.WeChat.Service.Impl;

import LapTrinhMang.WeChat.Dto.Response.UserOnlineStatusResponse;
import LapTrinhMang.WeChat.Dto.Response.UserResponse;
import LapTrinhMang.WeChat.Entity.AbstractEntity;
import LapTrinhMang.WeChat.Entity.User;
import LapTrinhMang.WeChat.Exception.BadRequestException;
import LapTrinhMang.WeChat.Exception.NotFoundException;
import LapTrinhMang.WeChat.Repository.UserRepository;
import LapTrinhMang.WeChat.Service.CentrifugoService;
import LapTrinhMang.WeChat.Service.UserService;
import LapTrinhMang.WeChat.Utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CentrifugoService centrifugoService;
    @Override
    public List<UserResponse> getUserByKeyword(String keyword) {
        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(()->new NotFoundException("User Not found"));

        List<User> userList = userRepository.findUserByKeyword(keyword,userId);
        if(userList.isEmpty()) return Collections.emptyList();

        //Lấy danh sách userId
        List<String> userIds = userList.stream().map(User::getId).toList();

        Map<String,Boolean> statusMap = centrifugoService.getUsersOnlineStatus(userIds);

        return userList.stream()
                .map(user -> UserResponse.convert(user, statusMap.getOrDefault(user.getId(),false)))//Nếu không có mặc định false
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getMyInfo() {
        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(()->new BadRequestException("Không lấy được user đang đăng nhập"));
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("User not found"));
        return UserResponse.convert(user,true);
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("User not found"));
        boolean online = centrifugoService.isUserOnline(userId);
        return UserResponse.convert(user,online);
    }
}
