package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(Long id, UserUpdateDto userUpdateDto);

    void delete(Long id);

    UserDto getUserById(Long id);

    List<UserDto> getAll();

}
