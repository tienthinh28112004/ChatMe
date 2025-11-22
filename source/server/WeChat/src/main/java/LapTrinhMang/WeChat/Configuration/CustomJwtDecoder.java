package LapTrinhMang.WeChat.Configuration;

import LapTrinhMang.WeChat.Entity.User;
import LapTrinhMang.WeChat.Exception.NotFoundException;
import LapTrinhMang.WeChat.Repository.UserRepository;
import LapTrinhMang.WeChat.Service.JwtService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${app.secret}")
    private String appSecret;
    private NimbusJwtDecoder nimbusJwtDecoder=null;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    @Override
    public Jwt decode(String token) throws JwtException {

        if(Objects.isNull(nimbusJwtDecoder)){
            SecretKeySpec secretKeySpec = new SecretKeySpec(appSecret.getBytes(),"HS512");
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();

        }
        try {
            String userId = jwtService.extractUserId(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new NotFoundException("User not found"));
            if(jwtService.verificationToken(token,user)){
                return nimbusJwtDecoder.decode(token);
            }
        } catch (ParseException | JOSEException e) {
            log.error("token hết hạn");
            throw new RuntimeException(e);
        }
        throw new JwtException("token invalid");
    }
}