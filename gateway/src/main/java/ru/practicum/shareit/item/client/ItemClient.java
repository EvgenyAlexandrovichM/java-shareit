package ru.practicum.shareit.item.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createItem(long userId, ItemCreateDto dto) {
        log.info("Gateway: create item by userId: {}", userId);
        return post("", userId, dto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemUpdateDto dto) {
        log.info("Gateway: update itemId: {} by userId: {}", itemId, userId);
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        log.info("Gateway: get itemId: {} for userId: {}", itemId, userId);
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByOwner(long userId) {
        log.info("Gateway: list items for owner userId: {}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> searchItems(long userId, String text) {
        log.info("Gateway: search items text: {} by userId: {}", text, userId);
        Map<String, Object> params = Map.of("text", text);
        return get("/search?text={text}", userId, params);
    }

    public ResponseEntity<Object> createComment(long userId,
                                                long itemId,
                                                CommentCreateDto dto) {
        log.info("Gateway: create comment for itemId: {} by userId: {}", itemId, userId);
        return post("/" + itemId + "/comment", userId, dto);
    }

    public ResponseEntity<Object> getComments(long userId, long itemId) {
        log.info("Gateway: get comments for itemId: {}", itemId);
        return get("/" + itemId + "/comment", userId);
    }
}

