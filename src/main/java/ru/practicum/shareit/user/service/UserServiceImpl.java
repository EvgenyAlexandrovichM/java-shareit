package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Creating user {}", userDto);

        if (userRepository.findUserByEmail(userDto.getEmail()).isPresent()) {
            log.warn("User with email {} already exists", userDto.getEmail());
            throw new ConflictException("User with email " + userDto.getEmail() + " already exists");
        }

        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long id, UserUpdateDto userUpdateDto) {
        log.info("Updating user with id {} and data {}", id, userUpdateDto);
        User existingUser = getUserOrThrow(id);

        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().equals(existingUser.getEmail())) {
            Optional<User> conflictingUser = userRepository.findUserByEmail(userUpdateDto.getEmail());

            if (conflictingUser.isPresent() && !conflictingUser.get().getId().equals(existingUser.getId())) {
                log.warn("Email {} is already taken by another user", userUpdateDto.getEmail());
                throw new ConflictException("User with email " + userUpdateDto.getEmail() + " already exists");
            }
            existingUser.setEmail(userUpdateDto.getEmail());
        }

        if (userUpdateDto.getName() != null) {
            existingUser.setName(userUpdateDto.getName());
        }
        userRepository.update(existingUser);
        return UserMapper.toUserDto(existingUser);
    }

    @Override
    public void delete(Long id) {
        log.info("Removing user with id {}", id);
        User existringUser = getUserOrThrow(id);
        userRepository.delete(existringUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        User user = getUserOrThrow(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        log.info("Getting all users");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }
}
