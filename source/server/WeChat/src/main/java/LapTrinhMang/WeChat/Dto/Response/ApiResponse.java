package LapTrinhMang.WeChat.Dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)//dùng để đánh dấu chuyển về Json
public class ApiResponse<T> {
    @Builder.Default
    private int code=1000;
    private String message;
    private T result;
}