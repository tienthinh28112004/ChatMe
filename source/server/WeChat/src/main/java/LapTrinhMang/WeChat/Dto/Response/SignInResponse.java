package LapTrinhMang.WeChat.Dto.Response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInResponse {
    private String userId;

    private String accessToken;

    private Long accessTokenExpireIn;

}
