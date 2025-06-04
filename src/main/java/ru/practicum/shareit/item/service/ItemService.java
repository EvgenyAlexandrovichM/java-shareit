package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    Item create(Item item);

    Item update(Long userId, Long itemId, Item item);

    Optional<Item> getItemById(Long id);

    List<Item> getItemsByOwnerId(Long id);

    List<Item> searchItems(String text);
}
