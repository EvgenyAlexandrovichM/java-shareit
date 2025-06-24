package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void shouldCreateUser() {
        UserDto userDto = new UserDto(null, "test@email.com", "Test User");
        User savedUser = new User(1L, "test@email.com", "Test User");

        when(userRepository.findUserByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto createdUserDto = userService.create(userDto);

        assertNotNull(createdUserDto);
        assertEquals(savedUser.getId(), createdUserDto.getId());
    }

    @Test
    void shouldThrowConflictExceptionWhenEmailExists() {
        UserDto userDto = new UserDto(null, "test@email.com", "Test User");
        User user = UserMapper.toUser(userDto);

        when(userRepository.findUserByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.create(userDto));
    }

    @Test
    void shouldUpdateUser() {
        long userId = 1L;
        UserUpdateDto userUpdateDto = new UserUpdateDto("new@email.com", "New Name");
        User existingUser = new User(userId, "old@email.com", "Old Name");
        User updatedUser = new User(userId, "new@email.com", "New Name");

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findUserByEmail(userUpdateDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.update(any(User.class))).thenReturn(updatedUser);

        UserDto updatedUserDto = userService.update(userId, userUpdateDto);

        assertNotNull(updatedUserDto);
        assertEquals(updatedUser.getEmail(), updatedUserDto.getEmail());
        assertEquals(updatedUser.getName(), updatedUserDto.getName());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdating() {
        long userId = 1L;
        UserUpdateDto userUpdateDto = new UserUpdateDto("new@email.com", "New Name");

        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(userId, userUpdateDto));
    }

    @Test
    void shouldRemoveUser() {
        long userId = 1L;
        User user = new User(userId, "test@email.com", "Test User");

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.delete(userId);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRemoving() {
        long userId = 1L;

        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete(userId));
    }

    @Test
    void shouldReturnUserIfExists() {
        long userId = 1L;
        User user = new User(userId, "test@email.com", "Test User");

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(userId);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGettingUserById() {
        long userId = 1L;

        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }
}