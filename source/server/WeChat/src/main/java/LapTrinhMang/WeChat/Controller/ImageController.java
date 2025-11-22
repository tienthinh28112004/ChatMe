package LapTrinhMang.WeChat.Controller;

import LapTrinhMang.WeChat.Dto.Response.ApiResponse;
import LapTrinhMang.WeChat.Dto.Response.UploadResponse;
import LapTrinhMang.WeChat.Utils.CloudinaryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ImageController {
    private final CloudinaryUtils cloudinaryUtils;

    @PostMapping("/uploadImage")
    public ApiResponse<UploadResponse> uploadImage(@RequestPart MultipartFile file){
        return ApiResponse.<UploadResponse>builder()
                .message("Nơi để upload các file ảnh,tệp")
                .result(cloudinaryUtils.uploadImage(file))
                .build();
    }
}
