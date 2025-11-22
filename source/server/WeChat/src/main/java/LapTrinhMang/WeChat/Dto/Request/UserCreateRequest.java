package LapTrinhMang.WeChat.Dto.Request;

import LapTrinhMang.WeChat.Validator.CustomPassword;
import LapTrinhMang.WeChat.Validator.MinSize;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    @MinSize(min=8)
    private String email;

    @NotBlank
    @CustomPassword()
    private String password;

}