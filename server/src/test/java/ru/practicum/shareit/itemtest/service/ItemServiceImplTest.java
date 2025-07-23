package ru.practicum.shareit.itemtest.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl service;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Ivan", "ivan@yandex.ru");
        item = new Item(1L,
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                owner,
                true,
                null,
                null,
                null);
    }

    @Test
    void createItem_success() {
        ItemCreateDto create = new ItemCreateDto("Saw", "Electric saw", true, null);

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(inv -> {
                    Item i = inv.getArgument(0);
                    i.setId(2L);
                    return i;
                });

        ItemResponseDto result = service.create(owner.getId(), create);

        assertEquals(2L, result.getId());
        assertEquals("Saw", result.getName());
        assertTrue(result.getAvailable());
        verify(itemRepository).save(argThat(i -> i.getOwner().equals(owner)));
    }

    @Test
    void createItem_throwNotFound() {
        ItemCreateDto create = new ItemCreateDto("Saw", "Electric saw", true, null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.create(2L, create)
        );
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_success() {
        ItemUpdateDto update = new ItemUpdateDto("Another hockey stick", null, false);

        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ItemResponseDto result = service.update(owner.getId(), item.getId(), update);

        assertEquals(item.getId(), result.getId());
        assertEquals("Another hockey stick", result.getName());
        assertFalse(result.getAvailable());
    }

    @Test
    void updateItem_throwNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.update(99L, item.getId(), new ItemUpdateDto())
        );
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_throwForbidden() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        assertThrows(
                ForbiddenException.class,
                () -> service.update(99L, item.getId(), new ItemUpdateDto())
        );
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemById_success() {
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId()))
                .thenReturn(List.of());

        ItemResponseDto result = service.getItemById(item.getId());

        assertEquals(item.getId(), result.getId());
        assertTrue(result.getComments().isEmpty());
    }

    @Test
    void getItemById_throwNotFound() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.getItemById(99L)
        );
    }

    @Test
    void getItemsByOwnerId_success() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerId(owner.getId()))
                .thenReturn(List.of(item));

        List<ItemResponseDto> result = service.getItemsByOwnerId(owner.getId());

        assertEquals(1, result.size());
        assertEquals(item.getName(), result.getFirst().getName());
    }

    @Test
    void getItemsByOwnerId_throwNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(
                NotFoundException.class,
                () -> service.getItemsByOwnerId(99L)
        );
    }

    @Test
    void searchItems_success() {
        when(itemRepository.searchItems("stick"))
                .thenReturn(List.of(item));

        List<ItemResponseDto> result = service.searchItems("stick");

        assertEquals(1, result.size());
        assertEquals("Hockey stick", result.getFirst().getName());
    }

    @Test
    void searchItems_EmptyTextShouldReturnEmptyList() {
        List<ItemResponseDto> empty = service.searchItems("");
        List<ItemResponseDto> blank = service.searchItems("       ");

        assertTrue(empty.isEmpty());
        assertTrue(blank.isEmpty());
        verify(itemRepository, never()).searchItems(anyString());
    }
}
