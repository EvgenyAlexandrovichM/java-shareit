package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
@Qualifier("inMemoryUserRepository")
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public User save(User user) {
        log.info("Saving user {}", user);
        long newId = idGenerator.incrementAndGet();
        user.setId(newId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Updating user {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(User user) {
        log.info("Removing user {}", user);
        users.remove(user.getId());
    }

    @Override
    public Optional<User> findUserById(Long id) {
        log.info("Finding user by id: {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return users.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        log.info("List of {} users", users.values().size());
        return new ArrayList<>(users.values());
    }
}
