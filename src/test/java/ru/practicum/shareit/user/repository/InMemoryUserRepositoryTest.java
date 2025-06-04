package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    void shouldSaveUser() {
        User user = new User(null, "test@email.com", "Test User");
        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());
        Optional<User> retrievedUser = userRepository.findUserById(savedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals(savedUser, retrievedUser.get());
    }

    @Test
    void shouldReturnUserIfExists() {
        User user = new User(null, "test@email.com", "Test User");
        User savedUser = userRepository.save(user);
        Optional<User> retrievedUser = userRepository.findUserById(savedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals(savedUser, retrievedUser.get());
    }

    @Test
    void shouldReturnEmptyIfNotExists() {
        Optional<User> retrievedUser = userRepository.findUserById(999L);
        assertFalse(retrievedUser.isPresent());
    }

    @Test
    void shouldUpdateUser() {
        User user = new User(null, "test@email.com", "Test User");
        User savedUser = userRepository.save(user);
        savedUser.setName("Updated Name");
        User updatedUser = userRepository.update(savedUser);
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void shouldDeleteUser() {
        User user = new User(null, "test@email.com", "Test User");
        User savedUser = userRepository.save(user);
        userRepository.delete(savedUser);
        Optional<User> retrievedUser = userRepository.findUserById(savedUser.getId());
        assertFalse(retrievedUser.isPresent());
    }
}