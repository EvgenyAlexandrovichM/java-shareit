package ru.practicum.shareit.usertest.repository;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;



import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private User user;

    @BeforeEach
    void addUsers() {
        repository.save(user = new User(1L, "Ivan", "ivan@yandex.ru"));
    }

    @AfterEach
    void deleteUsers() {
        repository.deleteAll();
    }

    @Test
    void findByEmail() {
        Optional<User> actualUser = repository.findByEmail("ivan@yandex.ru");

        assertTrue(actualUser.isPresent());
        assertEquals("Ivan", actualUser.get().getName());
        assertEquals("ivan@yandex.ru", actualUser.get().getEmail());
    }
}
