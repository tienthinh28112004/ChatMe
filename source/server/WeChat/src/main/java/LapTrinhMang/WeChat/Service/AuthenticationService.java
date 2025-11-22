package LapTrinhMang.WeChat.Service;

import LapTrinhMang.WeChat.Dto.Request.IntrospectRequest;
import LapTrinhMang.WeChat.Dto.Request.LogInRequest;
import LapTrinhMang.WeChat.Dto.Request.LogOutRequest;
import LapTrinhMang.WeChat.Dto.Request.UserCreateRequest;
import LapTrinhMang.WeChat.Dto.Response.IntrospectResponse;
import LapTrinhMang.WeChat.Dto.Response.RefreshTokenResponse;
import LapTrinhMang.WeChat.Dto.Response.SignInResponse;
import LapTrinhMang.WeChat.Dto.Response.UserResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;

import java.text.ParseException;

public interface AuthenticationService {
    UserResponse register(UserCreateRequest request);
    SignInResponse logIn(LogInRequest request, HttpServletResponse response);
    void logOut(LogOutRequest request, HttpServletResponse response) throws ParseException, JOSEException;
    RefreshTokenResponse refreshToken(@CookieValue(name = "refreshToken") String refreshToken) throws ParseException, JOSEException;
    IntrospectResponse introspect(IntrospectRequest request) throws ParseException;
    SignInResponse loginWithGoogle(String code,HttpServletResponse response);
}
