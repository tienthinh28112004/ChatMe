package LapTrinhMang.WeChat.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogOutRequest {
    @NotBlank
    private String accessToken;
}