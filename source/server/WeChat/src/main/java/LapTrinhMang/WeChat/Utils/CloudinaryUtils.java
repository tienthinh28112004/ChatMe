package LapTrinhMang.WeChat.Utils;

import LapTrinhMang.WeChat.Dto.Response.UploadResponse;
import LapTrinhMang.WeChat.Repository.UserRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryUtils {
    private final Cloudinary cloudinary;

    public UploadResponse uploadImage(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename(); // tên file gốc

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "upload",       // hoặc "/upload" nếu bạn đã dùng
                            "resource_type", "auto"   // ảnh, pdf, zip, video...
                    )
            );

            String url = result.get("secure_url").toString();

            return new UploadResponse(url, originalName);

        } catch (IOException e) {
            throw new RuntimeException("Upload fail: " + e.getMessage());
        }
    }
}
