package LapTrinhMang.WeChat.Dto.Response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)//đánh dấu chuyển về được json
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class ErrorResponse {
    private String message;
    private Map<String,String> items;
}