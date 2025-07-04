package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> createUser(
            @RequestBody @Valid UserCreateDto userCreateDto
    ) {
        log.info("Gateway: POST /users body: {}", userCreateDto);
        return client.createUser(userCreateDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @PathVariable @Positive Long userId,
            @RequestBody UserUpdateDto userUpdateDto
    ) {
        log.info("Gateway: PATCH /users/{} body: {}", userId, userUpdateDto);
        return client.updateUser(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable @Positive Long userId
    ) {
        log.info("Gateway: DELETE /users/{}", userId);
        return client.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(
            @PathVariable @Positive Long userId
    ) {
        log.info("Gateway: GET /users/{}", userId);
        return client.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Gateway: GET /users");
        return client.getAllUsers();
    }
}

