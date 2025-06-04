package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryItemRepositoryTest {

    private InMemoryItemRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryItemRepository();
    }

    @Test
    void shouldSaveItem() {
        User owner = new User(1L, "test@test.com", "Test");
        Item item = new Item(null, "Test Item", "Test Description", owner, true);

        Item savedItem = repository.save(item);

        assertNotNull(savedItem.getId());
        assertEquals(1L, savedItem.getId());
        Optional<Item> retrievedItem = repository.findItemById(savedItem.getId());
        assertTrue(retrievedItem.isPresent());
        assertEquals(savedItem, retrievedItem.get());
    }

    @Test
    void shouldReturnItemIfExists() {
        User owner = new User(1L, "test@test.com", "Test");
        Item item = new Item(null, "Test Item", "Test Description", owner, true);
        Item savedItem = repository.save(item);

        Optional<Item> retrievedItem = repository.findItemById(savedItem.getId());

        assertTrue(retrievedItem.isPresent());
        assertEquals(savedItem, retrievedItem.get());
    }

    @Test
    void shouldReturnEmptyIfNotExists() {
        Optional<Item> retrievedItem = repository.findItemById(999L);
        assertFalse(retrievedItem.isPresent());
    }
}