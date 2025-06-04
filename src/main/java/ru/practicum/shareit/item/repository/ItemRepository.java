package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Item update(Item item);

    Optional<Item> findItemById(Long id);

    List<Item> findItemsByOwnerId(Long id);

    List<Item> searchItems(String text);

}
