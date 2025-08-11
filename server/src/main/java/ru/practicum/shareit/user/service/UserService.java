package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserCreateDto userCreateDto);

    UserResponseDto update(Long id, UserUpdateDto userUpdateDto);

    void delete(Long id);

    UserResponseDto getUserById(Long id);

    List<UserResponseDto> getAll();

}
