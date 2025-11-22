package LapTrinhMang.WeChat.Repository.HttpClient;

import LapTrinhMang.WeChat.Dto.Request.ExchangeTokenRequest;
import LapTrinhMang.WeChat.Dto.Response.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
@FeignClient(name="outbound-identity",url = "https://oauth2.googleapis.com")
public interface OutBoundIdentityClient {
    @PostMapping(value = "/token",produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
}