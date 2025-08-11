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
        owner = userRepository.save(new User(null, "Ivan", "ivan@yandex.ru"));
        requester = userRepository.save(new User(null, "Alexander", "alexander@yandex.ru"));
        item1 = itemRepository.save(new Item(
                null,
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                owner,
                true,
                null,
                null,
                null));
        item2 = itemRepository.save(new Item(
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
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
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
        ItemRequest request = new ItemRequest(null,
                "Need a hockey stick",
                requester,
                LocalDateTime.now());
        itemRequestRepository.save(request);

        item1.setItemRequest(request);
        item1 = itemRepository.save(item1);

        List<Item> items = itemRepository.findByItemRequestId(request.getId());
        assertEquals(1, items.size());
        assertTrue(items.contains(item1));
    }

    @Test
    void findAllByItemRequestIdIn() {
        ItemRequest request1 = itemRequestRepository.save(new ItemRequest(
                null,
                "Need first stick",
                requester,
                LocalDateTime.now()
        ));
        ItemRequest request2 = itemRequestRepository.save(new ItemRequest(
                null,
                "Need seconds tick",
                requester,
                LocalDateTime.now()
        ));

        item1.setItemRequest(request1);
        item2.setItemRequest(request2);
        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> all = itemRepository.findAllByItemRequestIdIn(List.of(request1.getId(), request2.getId()));
        assertEquals(2, all.size());
        assertTrue(all.contains(item1));
        assertTrue(all.contains(item2));

        List<Item> single = itemRepository.findAllByItemRequestIdIn(List.of(request1.getId()));
        assertEquals(1, single.size());
        assertTrue(single.contains(item1));

        List<Item> empty = itemRepository.findAllByItemRequestIdIn(List.of(666L));
        assertTrue(empty.isEmpty());
    }
}
