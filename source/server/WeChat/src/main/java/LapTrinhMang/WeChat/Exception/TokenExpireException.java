package LapTrinhMang.WeChat.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpireException extends RuntimeException{
    public TokenExpireException(){
        super("Token hết hạn");
    }

    public TokenExpireException(String message){
        super(message);
    }
}