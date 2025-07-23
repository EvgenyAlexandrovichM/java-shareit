package ru.practicum.shareit.itemtest.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User owner;
    private Item item1;
    private Item item2;
    private User requester;

    @BeforeEach
    void setUp() {
        userRepository.save(owner = new User(null, "Ivan", "ivan@yandex.ru"));
        userRepository.save(requester = new User(null, "Alexander", "alexander@yandex.ru"));
        itemRepository.save(item1 = new Item(
                null,
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                owner,
                true,
                null,
                null,
                null));
        itemRepository.save(item2 = new Item(
                null,
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                owner,
                true,
                null,
                null,
                null));
    }

    @AfterEach
    void clear() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findByOwnerId() {
        List<Item> items = itemRepository.findByOwnerId(owner.getId());
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
    }

    @Test
    void searchItems() {
        User owner2 = new User(null, "Elena", "elena@yandex.ru");
        userRepository.save(owner2);

        Item item3 = new Item(null,
                "Drill",
                "Bad drill",
                owner2,
                true,
                null,
                null,
                null);
        itemRepository.save(item3);

        List<Item> items = itemRepository.searchItems("Hockey stick");
        assertEquals(2, items.size());
        assertTrue(items.contains(item1));
        assertFalse(items.contains(item3));
    }

    @Test
    void findByItemRequestId() {
        userRepository.save(requester);
        ItemRequest request = new ItemRequest(null,
                "Need a hockey stick",
                requester,
                LocalDateTime.now());
        itemRequestRepository.save(request);

        item1.setItemRequest(request);
        itemRepository.save(item1);

        List<Item> items = itemRepository.findByItemRequestId(request.getId());
        assertEquals(1, items.size());
        assertTrue(items.contains(item1));
    }
}
