package ru.practicum.shareit.itemtest.commenttest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.comment.service.CommentServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CommentServiceImpl service;

    private User author;
    private User owner;
    private Item item;
    private Booking pastBooking;
    private CommentCreateDto createdComment;

    @BeforeEach
    void setUp() {
        author = new User(1L, "Ivan", "ivan@yandex.ru");
        owner = new User(2L, "Andrey", "andrey@yandex.ru");
        item = new Item(1L,
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                owner,
                true,
                null,
                null,
                null);
        pastBooking = new Booking();
        pastBooking.setId(1L);
        pastBooking.setBooker(author);
        pastBooking.setItem(item);
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(1));
        createdComment = new CommentCreateDto("Great stick");
    }
    @Test
    void createComment_success() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
        when(bookingRepository.findCompletedBookings(item.getId(), author.getId()))
                .thenReturn(List.of(pastBooking));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(inv -> {
                    Comment c = inv.getArgument(0);
                    c.setId(99L);
                    return c;
                });

        CommentResponseDto result = service.createComment(author.getId(), item.getId(), createdComment);

        assertEquals(99L, result.getId());
        assertEquals("Great stick", result.getText());
        assertEquals(author.getName(), result.getAuthorName());
        assertNotNull(result.getCreated());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_throwNotFoundOnItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.createComment(author.getId(), 99L, createdComment)
        );
        verifyNoInteractions(bookingRepository, commentRepository);
    }

    @Test
    void createComment_throwNotFoundOnUser() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.createComment(99L, item.getId(), createdComment)
        );
        verifyNoInteractions(bookingRepository, commentRepository);
    }

    @Test
    void createComment_throwBadRequest() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(author.getId()))
                .thenReturn(Optional.of(author));
        when(bookingRepository.findCompletedBookings(item.getId(), author.getId()))
                .thenReturn(List.of());

        assertThrows(
                BadRequestException.class,
                () -> service.createComment(author.getId(), item.getId(), createdComment)
        );


        verify(commentRepository, never()).save(any());
    }

    @Test
    void getCommentsForItem_success() {
        Comment c1 = new Comment();
        c1.setId(1L);
        c1.setText("Awesome");
        c1.setAuthor(author);
        c1.setItem(item);
        c1.setCreated(LocalDateTime.now().minusDays(2));

        Comment c2 = new Comment();
        c2.setId(2L);
        c2.setText("Huge stick");
        c2.setAuthor(author);
        c2.setItem(item);
        c2.setCreated(LocalDateTime.now().minusDays(5));

        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of(c1, c2));

        List<CommentResponseDto> result = service.getCommentsForItem(item.getId());

        assertEquals(2, result.size());
        assertEquals("Awesome", result.getFirst().getText());
        assertEquals("Huge stick", result.get(1).getText());
    }

    @Test
    void getCommentsForItem_EmptyTextShouldReturnEmptyList() {
        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of());

        List<CommentResponseDto> result = service.getCommentsForItem(item.getId());

        assertTrue(result.isEmpty());
    }
}
