package ru.practicum.shareit.itemtest.commenttest.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;
    private User owner;
    private Item item1;
    private Item item2;
    private Comment c1;
    private Comment c2;
    private Comment c3;

    @BeforeEach
    void setUp() {
        author = userRepository.save(new User(null, "ivan", "ivan@yandex.ru"));
        owner = userRepository.save(new User(null, "andrey", "andrey@yandex.ru"));
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

        commentRepository.save(c1 = new Comment(
                null,
                "Awesome",
                item1,
                author,
                LocalDateTime.now().minusDays(1)));

        commentRepository.save(c2 = new Comment(
                null,
                "Huge stick",
                item1,
                author,
                LocalDateTime.now().minusDays(3)
        ));

        commentRepository.save(c3 = new Comment(
                null,
                "Wonderfully stick",
                item2,
                author,
                LocalDateTime.now()
        ));
    }

    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item1.getId());

        assertEquals(2, comments.size());
        assertTrue(comments.stream()
                .allMatch(c -> c.getItem().getId().equals(item1.getId())));
    }

    @Test
    void findByItemIdIn() {
        List<Comment> twoItems = commentRepository.findByItemIdIn(List.of(item1.getId(), item2.getId()));
        assertEquals(3, twoItems.size());

        List<Comment> onlyItem1 = commentRepository.findByItemIdIn(List.of(item1.getId()));
        assertEquals(2, onlyItem1.size());
        assertEquals("Awesome", onlyItem1.getFirst().getText());
    }
}
