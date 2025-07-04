package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;



@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserCreateDto userCreateDto) {
        log.info("Server: Request to create user: {}", userCreateDto);
        UserResponseDto createdUser = service.create(userCreateDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long userId,
                                                      @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Server: Request to update user with id: {} and data: {}", userId, userUpdateDto);
        UserResponseDto updatedUser = service.update(userId, userUpdateDto);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Server: Request to remove user with id: {}", userId);
        service.delete(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        log.info("Server: Request to get user with id: {}", userId);
        UserResponseDto userResponse = service.getUserById(userId);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        log.info("Server: Request to get all users");
        List<UserResponseDto> users = service.getAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
