package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;


public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    ItemDto getItemById(Long id);

    List<ItemDto> getItemsByOwnerId(Long id);

    List<ItemDto> searchItems(String text);
}
