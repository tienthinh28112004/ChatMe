package LapTrinhMang.WeChat.Dto.Response;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntrospectResponse {
    private boolean valid;

}
