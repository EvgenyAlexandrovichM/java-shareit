package ru.practicum.shareit.request.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.Map;

@Service
@Slf4j
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl,
                             RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createRequest(long userId, ItemRequestCreateDto dto) {
        log.info("Gateway: create item request by userId: {}", userId);
        return post("", userId, dto);
    }

    public ResponseEntity<Object> getRequestsByUser(long userId) {
        log.info("Gateway: get own item requests for userId: {}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(long userId, int from, int size) {
        log.info("Gateway: get all item requests userId: {}, from: {}, size: {}", userId, from, size);
        Map<String, Object> params = Map.of("from", from, "size", size);
        return get("/all?from={from}&size={size}", userId, params);
    }

    public ResponseEntity<Object> getRequestById(long userId, long requestId) {
        log.info("Gateway: get item request id: {} for userId: {}", requestId, userId);
        return get("/" + requestId, userId);
    }
}
