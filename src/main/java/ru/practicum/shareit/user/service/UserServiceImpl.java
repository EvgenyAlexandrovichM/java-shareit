package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateDto userCreateDto) {
        log.info("Creating user: {}", userCreateDto);
        if (userRepository.findByEmail(userCreateDto.getEmail()).isPresent()) {
            log.warn("User with email: {} already exists", userCreateDto.getEmail());
            throw new ConflictException("User with email " + userCreateDto.getEmail() + " already exists");
        }
        User user = UserMapper.toUser(userCreateDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto userUpdateDto) {
        log.info("Updating user with id: {} and data: {}", id, userUpdateDto);
        User existingUser = getUserOrThrow(id);
        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().equals(existingUser.getEmail())) {
            Optional<User> conflictingUser = userRepository.findByEmail(userUpdateDto.getEmail());

            if (conflictingUser.isPresent() && !conflictingUser.get().getId().equals(existingUser.getId())) {
                log.warn("Email: {} is already taken by another user", userUpdateDto.getEmail());
                throw new ConflictException("User with email " + userUpdateDto.getEmail() + " already exists");
            }
        }
        User updatedUser = UserMapper.toUser(existingUser, userUpdateDto);
        userRepository.save(updatedUser);
        return UserMapper.toResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Removing user with id: {}", id);
        User existringUser = getUserOrThrow(id);
        userRepository.delete(existringUser);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        User user = getUserOrThrow(id);
        return UserMapper.toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> getAll() {
        log.info("Getting all users");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }
}
