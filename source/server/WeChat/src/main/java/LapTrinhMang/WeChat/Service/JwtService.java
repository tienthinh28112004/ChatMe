package LapTrinhMang.WeChat.Service;

import LapTrinhMang.WeChat.Entity.User;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractUserId(String accessToken) throws ParseException;
    boolean verificationToken(String token,User user) throws ParseException, JOSEException;
    long extractTokenExpired(String token);
}
