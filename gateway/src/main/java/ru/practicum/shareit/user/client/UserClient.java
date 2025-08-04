package ru.practicum.shareit.user.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Service
@Slf4j
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createUser(UserCreateDto dto) {
        log.info("Gateway: create user: {}", dto);
        return post("", dto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserUpdateDto dto) {
        log.info("Gateway: update user id: {} data: {}", userId, dto);
        return patch("/" + userId, dto);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        log.info("Gateway: delete user id: {}", userId);
        return delete("/" + userId);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        log.info("Gateway: get user by id: {}", userId);
        return get("/" + userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        log.info("Gateway: get all users");
        return get("");
    }
}
