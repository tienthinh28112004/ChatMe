package LapTrinhMang.WeChat.Service.Impl;

import LapTrinhMang.WeChat.Dto.Response.PresenceStatsResponse;
import LapTrinhMang.WeChat.Dto.Response.RoomResponse;
import LapTrinhMang.WeChat.Dto.Response.UserOnlineStatusResponse;
import LapTrinhMang.WeChat.Exception.BadRequestException;
import LapTrinhMang.WeChat.Exception.NotFoundException;
import LapTrinhMang.WeChat.Repository.RoomMemberRepository;
import LapTrinhMang.WeChat.Service.CentrifugoService;
import LapTrinhMang.WeChat.Utils.SecurityUtils;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CentrifugoServiceImpl implements CentrifugoService {

    @Value("${centrifugo.jwt-secret}")
    private String jwtSecret;

    @Value("${centrifugo.api-url}")
    private String apiUrl;

    @Value("${centrifugo.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final RoomMemberRepository roomMemberRepository;

    @Override
    public String createConnectionToken() {
        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(() -> new NotFoundException("User not found"));

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId)
                .issuer("Tienthinh")  // chỉ dùng ASCII
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 60 * 60_000L)) // 1h
                .build();

        JWSObject jwsObject = new JWSObject(header, new Payload(claimsSet.toJSONObject()));

        try {
            jwsObject.sign(new MACSigner(jwtSecret));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    public String createSubcriptionToken(String channel) {
        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(() -> new NotFoundException("User not found"));

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId)
                .issuer("Tienthinh")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 30 * 60_000L)) // 30 phút
                .claim("channel", channel)
                .build();

        JWSObject jwsObject = new JWSObject(header, new Payload(claimsSet.toJSONObject()));

        try {
            jwsObject.sign(new MACSigner(jwtSecret));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return jwsObject.serialize();
    }

    @Override
    public void broadcast(List<String> channels, Object data, String idempotencyKey) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/api/broadcast")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);
        headers.set("X-Centrifugo-Error-Mode", "transport");

        Map<String, Object> body = Map.of(
                "channels", channels,
                "data", data,
                "idempotency_key", idempotencyKey
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new BadRequestException("Kết nối Centrifugo thất bại " + response.getBody());
        }
    }

    @Override
    public boolean isUserOnline(String userId) {
        Map<String, Boolean> map = getUsersOnlineStatus(List.of(userId));
        return map.getOrDefault(userId, false);
    }

    @Override
    public int getRoomOnlineCount(String roomId) {
        String channel = "room#" + roomId;

        String url = UriComponentsBuilder.fromUri(URI.create(apiUrl))
                .path("/api/presence_stats")
                .build(true)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);
        headers.set("X-Centrifugo-Error-Mode", "transport");

        Map<String, Object> body = Map.of("channel", channel);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<PresenceStatsResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, request, PresenceStatsResponse.class);
        if (!response.getStatusCode().is2xxSuccessful()
                || response.getBody() == null
                || response.getBody().getResult() == null) {
            return 0;
        }
        return response.getBody().getResult().getNum_clients();
    }

    @Override
    public Map<String, Boolean> getUsersOnlineStatus(List<String> userIds) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/api/get_user_status")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-KEY", apiKey);

        Map<String, Object> body = Map.of("users", userIds);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<UserOnlineStatusResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, request, UserOnlineStatusResponse.class);
        Map<String, Boolean> resultMap = new HashMap<>();

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            UserOnlineStatusResponse.Result result = response.getBody().getResult();
            if (result != null && result.getStatuses() != null) {
                long now = Instant.now().getEpochSecond();
                long ONLINE_THRESHOLD = 30; // 30s gần nhất coi là online
                for (UserOnlineStatusResponse.UserStatus status : result.getStatuses()) {
                    boolean isOnline = false;
                    if (status.getOnline() != null) {
                        isOnline = (now - status.getOnline()) <= ONLINE_THRESHOLD;
                    }
                    resultMap.put(status.getUser(), isOnline);
                }
            }
        }
        return resultMap;
    }


    public void userStatusChanged(boolean online) {
        String userId = SecurityUtils.getCurrentLogin()
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<String> roomIds = roomMemberRepository.findRoomIdsByUser(userId);
        if (roomIds.isEmpty()) {
            log.info("No rooms for this user, stop.");
            return;
        }

        for (String roomId : roomIds) {
            List<String> memberIds = roomMemberRepository.findUserIdsByRoomId(roomId);

            Map<String, Boolean> statusMap = getUsersOnlineStatus(memberIds);

            long onlineCount = statusMap.values().stream()
                    .filter(Boolean::booleanValue)
                    .count();
            boolean roomOnline = onlineCount > 0;

            List<String> channels = memberIds.stream()
                    .map(id -> "personal#" + id)
                    .toList();

            Map<String, Object> data = Map.of(
                    "type", "room_status_changed",
                    "roomId", roomId,
                    "online", roomOnline,
                    "onlineCount", onlineCount
            );

            broadcast(channels, data, "user_status_" + roomId+"_" + System.currentTimeMillis());
        }
    }

    @Override
    public void broadcastRoomCreated(List<String> userIds, RoomResponse room) {

        if (userIds == null || userIds.isEmpty()) return;

        List<String> channels = userIds.stream()
                .map(id -> "personal#" + id)
                .toList();

        Map<String, Object> data = Map.of(
                "type", "room_created",
                "room", room
        );

        broadcast(
                channels,
                data,
                "room_created_" + room.getId() + "_" + System.currentTimeMillis()
        );
    }

}
