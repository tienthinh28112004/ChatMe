package LapTrinhMang.WeChat.Dto.Response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatusResponse {
    private Result result;
    @Data
    public static class Result{
        private String user;//id user
        private boolean online;//trạng thái user
        private int client;//số lượng kết nối đang mở của user
    }
}
