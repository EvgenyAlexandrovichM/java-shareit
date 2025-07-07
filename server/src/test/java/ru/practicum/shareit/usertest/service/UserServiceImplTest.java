package ru.practicum.shareit.usertest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    private User user;

    @BeforeEach
    void setup() {
        user = new User(1L, "Ivan", "ivan@yandex.ru");
    }

    @Test
    void createUser_success() {
        UserCreateDto create = new UserCreateDto("Oleg", "oleg@yandex.ru");
        when(repository.findByEmail(create.getEmail())).thenReturn(Optional.empty());
        when(repository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        UserResponseDto result = service.create(create);

        assertEquals(2L, result.getId());
        assertEquals("Oleg", result.getName());
        assertEquals("oleg@yandex.ru", result.getEmail());
        verify(repository).save(any(User.class));
    }

    @Test
    void createUser_throwConflict() {
        UserCreateDto create = new UserCreateDto("Ivan", user.getEmail());
        when(repository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        ConflictException exception = assertThrows(
                ConflictException.class, () -> service.create(create)
        );
        assertTrue(exception.getMessage().contains("already exists"));
        verify(repository, never()).save(any());
    }

    @Test
    void updateUserNameOnly_success() {
        UserUpdateDto update = new UserUpdateDto("NewIvan", null);
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDto output = service.update(1L, update);

        assertEquals("NewIvan", output.getName());
        assertEquals("ivan@yandex.ru", output.getEmail());
        verify(repository).save(argThat(u -> "NewIvan".equals(u.getName())));
    }

    @Test
    void updateUserEmailOnly_success() {
        UserUpdateDto update = new UserUpdateDto(null, "ivan@mail.ru");
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findByEmail(update.getEmail())).thenReturn(Optional.empty());
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDto output = service.update(1L, update);

        assertEquals("ivan@mail.ru", output.getEmail());
        verify(repository).save(argThat(u -> "ivan@mail.ru".equals(u.getEmail())));
    }

    @Test
    void updateUser_throwConflict() {
        User otherUser = new User(2L, "Elena", "elena@mail.ru");
        UserUpdateDto update = new UserUpdateDto(null, otherUser.getEmail());

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.findByEmail(otherUser.getEmail()))
                .thenReturn(Optional.of(otherUser));

        assertThrows(
                ConflictException.class,
                () -> service.update(1L, update)
        );
        verify(repository, never()).save(any());
    }

    @Test
    void updateUser_throwNotFound() {
        when(repository.findById(50L)).thenReturn(Optional.empty());
        assertThrows(
                NotFoundException.class,
                () -> service.update(50L, new UserUpdateDto(null, null))
        );
    }

    @Test
    void getUserById_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        UserResponseDto output = service.getUserById(1L);
        assertEquals(1L, output.getId());
    }

    @Test
    void getUserById_throwNotFound() {
        when(repository.findById(50L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getUserById(50L));
    }

    @Test
    void getAllUsers_success() {
        User user2 = new User(2L, "Elena", "elena@yandex.ru");
        when(repository.findAll()).thenReturn(List.of(user, user2));

        List<UserResponseDto> list = service.getAll();
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(u -> "Elena".equals(u.getName())));
    }

    @Test
    void deleteUser_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        service.delete(1L);
        verify(repository).delete(user);
    }

    @Test
    void deleteUser_throwNotFound() {
        when(repository.findById(50L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getUserById(50L));
    }
}
