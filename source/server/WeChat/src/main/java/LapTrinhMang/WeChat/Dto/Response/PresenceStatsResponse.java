package LapTrinhMang.WeChat.Dto.Response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PresenceStatsResponse {
    private Result result;

    @Data
    public static class Result{
        private int num_clients;//số user duy nhất
        private int num_users;//tổng số connection kết nối
    }
}
