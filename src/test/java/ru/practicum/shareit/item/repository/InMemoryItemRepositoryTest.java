package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
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

    @Test
    void shouldReturnAllItems() {
        User owner = new User(1L, "test@test.com", "Test");
        Item item1 = new Item(null, "Item 1", "Description 1", owner, true);
        Item item2 = new Item(null, "Item 2", "Description 2", owner, false);

        repository.save(item1);
        repository.save(item2);

        List<Item> allItems = repository.findAll();

        assertEquals(2, allItems.size());
        assertEquals("Item 1", allItems.get(0).getName());
        assertEquals("Item 2", allItems.get(1).getName());
    }

    @Test
    void shouldReturnItemsForOwner() {
        User owner1 = new User(1L, "test1@test.com", "Test 1");
        User owner2 = new User(2L, "test2@test.com", "Test 2");
        Item item1 = new Item(null, "Item 1", "Description 1", owner1, true);
        Item item2 = new Item(null, "Item 2", "Description 2", owner2, false);

        repository.save(item1);
        repository.save(item2);

        List<Item> itemsForOwner1 = repository.findItemsByOwnerId(1L);

        assertEquals(1, itemsForOwner1.size());
        assertEquals("Item 1", itemsForOwner1.getFirst().getName());
    }

    @Test
    void shouldReturnItemsContainingText() {
        User owner = new User(1L, "test@test.com", "Test");
        Item item1 = new Item(null, "Test Item", "Description 1", owner, true);
        Item item2 = new Item(null, "Another Item", "Test Description 2", owner, false);

        repository.save(item1);
        repository.save(item2);

        List<Item> searchResults = repository.searchItems("test");

        assertEquals(1, searchResults.size());
        assertEquals("Test Item", searchResults.getFirst().getName());
    }
}
