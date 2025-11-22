package LapTrinhMang.WeChat.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenExpireException extends RuntimeException{
    public RefreshTokenExpireException(){
        super("Refresh token thất bại");
    }
    public RefreshTokenExpireException(String message){
        super(message);
    }
}