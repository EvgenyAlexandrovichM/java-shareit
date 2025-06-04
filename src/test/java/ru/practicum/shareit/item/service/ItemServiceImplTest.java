package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository);
    }

    @Test
    void shouldCreateItem() {
        Long userId = 1L;
        User owner = new User(userId, "test@test.com", "Test");
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", owner, true);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item savedItem = new Item(1L, "Test Item", "Test Description", owner, true);

        when(userRepository.findUserById(userId)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto createdItemDto = itemService.create(userId, itemDto);

        assertNotNull(createdItemDto);
        assertEquals(savedItem.getId(), createdItemDto.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenOwnerNotFound() {
        Long userId = 1L;
        User owner = new User(userId, "test@test.com", "Test");
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", owner, true);

        when(userRepository.findUserById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(userId, itemDto));
    }

    @Test
    void shouldUpdateItem() {
        Long userId = 1L;
        Long itemId = 1L;
        User owner = new User(userId, "test@test.com", "Test");
        Item existingItem = new Item(itemId, "Old Name", "Old Description", owner, true);
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("New Name", "New Description", false);

        when(itemRepository.findItemById(itemId)).thenReturn(Optional.of(existingItem));

        ItemDto updatedItemDto = itemService.update(userId, itemId, itemUpdateDto);

        assertNotNull(updatedItemDto);
        assertEquals("New Name", updatedItemDto.getName());
        assertEquals("New Description", updatedItemDto.getDescription());
        assertFalse(updatedItemDto.getAvailable());
    }

    @Test
    void shouldThrowForbiddenExceptionWhenUserIsNotOwner() {
        Long userId = 1L;
        Long itemId = 1L;
        User owner = new User(2L, "test@test.com", "Test");
        Item existingItem = new Item(itemId, "Old Name", "Old Description", owner, true);
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("New Name", "New Description", false);

        when(itemRepository.findItemById(itemId)).thenReturn(Optional.of(existingItem));

        assertThrows(ForbiddenException.class, () -> itemService.update(userId, itemId, itemUpdateDto));
    }

    @Test
    void shouldReturnItemIfExists() {
        Long itemId = 1L;
        User owner = new User(1L, "test@test.com", "Test");
        Item item = new Item(itemId, "Test Item", "Test Description", owner, true);

        when(itemRepository.findItemById(itemId)).thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.getItemById(itemId);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFound() {
        Long itemId = 1L;

        when(itemRepository.findItemById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId));
    }
}