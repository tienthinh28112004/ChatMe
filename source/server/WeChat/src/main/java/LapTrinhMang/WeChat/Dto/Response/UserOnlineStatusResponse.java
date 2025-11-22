package LapTrinhMang.WeChat.Dto.Response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOnlineStatusResponse {
    private Result result;
    @Data
    public static class Result{
        private List<UserStatus> statuses;
    }
    @Data
    public static class UserStatus{
        private String user;
        private Long online;
        private Long active;
    }
}
