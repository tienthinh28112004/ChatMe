package LapTrinhMang.WeChat.Utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;


public class SecurityUtils {
    private SecurityUtils(){//đây là lớp tiện iích chỉ hỗ trợ xử lí các phương pháp chung
    }//nên phải có cái này để tránh khai báo được ecurityUtils utils = new SecurityUtils(); từ bên ngoài
    public static Optional<String> getCurrentLogin(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication=context.getAuthentication();

        if(authentication.getPrincipal() instanceof UserDetails userDetails){
            return Optional.ofNullable(userDetails.getUsername());
        }
        if(authentication.getPrincipal() instanceof Jwt jwt){
            return Optional.ofNullable(jwt.getSubject());
        }
        if(authentication.getPrincipal() instanceof String s){
            return Optional.of(s);
        }
        //tất cả đều trả ra userId
        return Optional.empty();
    }
}