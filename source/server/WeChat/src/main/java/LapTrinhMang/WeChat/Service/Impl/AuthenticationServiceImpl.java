package LapTrinhMang.WeChat.Service.Impl;

import LapTrinhMang.WeChat.Dto.Request.*;
import LapTrinhMang.WeChat.Dto.Response.*;
import LapTrinhMang.WeChat.Entity.Message;
import LapTrinhMang.WeChat.Entity.RoomMember;
import LapTrinhMang.WeChat.Entity.User;
import LapTrinhMang.WeChat.Exception.BadRequestException;
import LapTrinhMang.WeChat.Exception.NotFoundException;
import LapTrinhMang.WeChat.Exception.RefreshTokenExpireException;
import LapTrinhMang.WeChat.Exception.TokenExpireException;
import LapTrinhMang.WeChat.Repository.HttpClient.OutBoundIdentityClient;
import LapTrinhMang.WeChat.Repository.HttpClient.OutBoundUserClient;
import LapTrinhMang.WeChat.Repository.UserRepository;
import LapTrinhMang.WeChat.Service.AuthenticationService;
import LapTrinhMang.WeChat.Service.JwtService;
import LapTrinhMang.WeChat.Service.RedisService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final OutBoundIdentityClient outBoundIdentityClient;
    private final OutBoundUserClient outBoundUserClient;

    @Value("${app.jwt.token.expires-in}")
    private Long accessTokenExpireIn;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final String grant_Type = "authorization_code";

    @Override
    public UserResponse register(UserCreateRequest request) {
        boolean exists = userRepository.existsByEmail(request.getEmail());
        User user=null;
        if(exists){
            throw new BadRequestException("Tài khoản đã tồn tại");
        }else{
            List<RoomMember> roomMemberList = new ArrayList<>();
            List<Message> messageList = new ArrayList<>();
            user= User.builder()
                .userName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .avatar("https://i.pinimg.com/236x/5e/e0/82/5ee082781b8c41406a2a50a0f32d6aa6.jpg")
                .lastSeen(null)
                .roomMemberships(roomMemberList)
                .messages(messageList)
                .build();
            userRepository.save(user);
        }
        userRepository.save(user);
        return UserResponse.convert(user,true);
    }

    @Override
    public SignInResponse logIn(LogInRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Thông tin đăng nhập không hợp lệ, mật khẩu không đúng");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        userRepository.save(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setHttpOnly(true);//để true
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setSecure(false);//true thì chỉ gửi qua https thôi

        response.addCookie(cookie);
        return SignInResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .accessTokenExpireIn(accessTokenExpireIn)
                .build();
    }

    @Override
    public void logOut(LogOutRequest request, HttpServletResponse response) throws ParseException {
        if (StringUtils.isBlank(request.getAccessToken())) {
            throw new TokenExpireException("Token không hợp lệ");
        }
        String userId = jwtService.extractUserId(request.getAccessToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        long accessTokenExpireIn = jwtService.extractTokenExpired(request.getAccessToken());
        if (accessTokenExpireIn > 0) {
            String jwtId = SignedJWT.parse(request.getAccessToken()).getJWTClaimsSet().getJWTID();
            redisService.save(jwtId, request.getAccessToken(), accessTokenExpireIn, TimeUnit.MILLISECONDS);
            userRepository.save(user);
        }

        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setSecure(false);//chỉ https mới truyền được
        cookie.setPath("/");
        cookie.setHttpOnly(true);//đánh dấu httpOnly
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Override
    public RefreshTokenResponse refreshToken(String refreshToken) throws ParseException, JOSEException {
        if (StringUtils.isBlank(refreshToken)) {
            throw new BadCredentialsException("RefreshToken không hợp lệ");
        }
        String userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!jwtService.verificationToken(refreshToken, user)) {
            throw new RefreshTokenExpireException("RefreshToken hết hạn");
        }
        String accessToken = jwtService.generateAccessToken(user);
        return RefreshTokenResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException {
        String userId = jwtService.extractUserId(request.getAccessToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        boolean valid = true;
        try {
            jwtService.verificationToken(request.getAccessToken(), user);
        } catch (JOSEException e) {
            valid = false;
        }
        return IntrospectResponse.builder()
                .valid(valid)
                .build();
    }

    @Override
    public SignInResponse loginWithGoogle(String code, HttpServletResponse response) {

        ExchangeTokenResponse result = outBoundIdentityClient.exchangeToken(ExchangeTokenRequest.builder()
                .code(code)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri)
                .grantType(grant_Type)
                .build());

        GoogleUserResponse getUserInfo = outBoundUserClient.getUserInfo("json", result.getAccessToken());

        User user = User.builder()
                .email(getUserInfo.getEmail())
                .userName(getUserInfo.getName())
                .avatar(getUserInfo.getPicture())
                .build();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setSecure(false);//true để tránh https
        cookie.setDomain("localhost");

        response.addCookie(cookie);

        return SignInResponse.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .accessTokenExpireIn(accessTokenExpireIn)
                .build();
    }
}