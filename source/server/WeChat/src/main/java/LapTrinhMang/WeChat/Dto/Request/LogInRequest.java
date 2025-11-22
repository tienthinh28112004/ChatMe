package LapTrinhMang.WeChat.Dto.Request;

import LapTrinhMang.WeChat.Validator.CustomPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogInRequest {
    @NotBlank
    private String email;

    @NotBlank
    @CustomPassword
    private String password;
}