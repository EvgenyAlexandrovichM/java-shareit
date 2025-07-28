package ru.practicum.shareit.itemrequest.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requester;
    private User otherUser;

    @BeforeEach
    void setUp() {
        requester = userRepository.save(new User(
                null,
                "Ivan",
                "ivan@yandex.ru")
        );
        otherUser = userRepository.save(new User(
                null,
                "Andrey",
                "andrey@yandex.ru")
        );
    }

    @AfterEach
    void clear() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByRequesterIdOrderByCreatedDesc() {
        ItemRequest request1 = itemRequestRepository.save(
                new ItemRequest(
                        null,
                        "First request",
                        requester,
                        LocalDateTime.now().minusDays(2)
                )
        );
        ItemRequest request2 = itemRequestRepository.save(
                new ItemRequest(
                        null,
                        "Second request",
                        requester,
                        LocalDateTime.now().minusDays(1)
                )
        );
        ItemRequest request3 = itemRequestRepository.save(
                new ItemRequest(
                        null,
                        "Third reuqest",
                        requester,
                        LocalDateTime.now()
                )
        );
        List<ItemRequest> result = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requester.getId());

        assertEquals(3, result.size());
        assertEquals(request3.getId(), result.getFirst().getId());
        assertEquals(request2.getId(), result.get(1).getId());
        assertEquals(request1.getId(), result.get(2).getId());
        assertEquals(request1.getRequester(), requester);
    }

    @Test
    void findByRequesterIdNot_withPaginationAndSorting() {
        itemRequestRepository.save(
                new ItemRequest(
                        null,
                        "Owner's request",
                        requester,
                        LocalDateTime.now().minusDays(2)
                )
        );
        ItemRequest request1 = itemRequestRepository.save(
                new ItemRequest(
                        null,
                        "Other user request",
                        otherUser,
                        LocalDateTime.now().minusDays(1)
                )
        );
        ItemRequest request2 = itemRequestRepository.save(
                new ItemRequest(
                        null,
                        "Other user request2",
                        otherUser,
                        LocalDateTime.now()
                )
        );

        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("created").descending());
        Page<ItemRequest> page = itemRequestRepository.findByRequesterIdNot(requester.getId(), pageRequest);

        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getNumberOfElements());
        assertEquals(request2.getId(), page.getContent().getFirst().getId());
    }
}
