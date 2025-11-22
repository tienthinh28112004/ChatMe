package LapTrinhMang.WeChat.Service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    void save(String key,String value);//lưu cặp key-value vào redis
    void setTimeToLive(String key, long timeoutInDays);//lưu thời gian cho key tồn tại
    void save(String key, String value, long duration, TimeUnit timeUnit);
    String get(String key);
    void delete(String key);
}
