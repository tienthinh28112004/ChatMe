package LapTrinhMang.WeChat.Dto.Response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GoogleUserResponse {
    String id;//id người dùng trên google
    String email;//địa chỉ email của người dùng
    boolean verified;//true nếu email được google xác minh(có thể tin tưởng)
    String name;//họ tên đầy đủ của người dùng
    String picture;//ảnh đại diện của người dùng
    String locale;//ngôn ngữ ưu tiên của người dùng(en,vi,..)
}