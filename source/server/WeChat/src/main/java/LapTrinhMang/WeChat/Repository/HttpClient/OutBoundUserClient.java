package LapTrinhMang.WeChat.Repository.HttpClient;

import LapTrinhMang.WeChat.Dto.Response.GoogleUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "outbound-user-client",url = "http://www.googleapis.com")
public interface OutBoundUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    GoogleUserResponse getUserInfo(@RequestParam("alt") String alt,
                                   @RequestParam("access_token") String accessToken);
}