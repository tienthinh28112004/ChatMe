package LapTrinhMang.WeChat.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(){
        super("Truy cập không hợp lệ");
    }
    public AccessDeniedException(String message){
        super(message);
    }
}