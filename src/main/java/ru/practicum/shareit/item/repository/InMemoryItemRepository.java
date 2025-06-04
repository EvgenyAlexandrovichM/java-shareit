package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Slf4j
@Qualifier("inMemoryItemRepository")
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Item save(Item item) {
        log.info("Saving item {}", item);
        long newId = idGenerator.incrementAndGet();
        item.setId(newId);
        items.put(item.getId(), item);
        log.info("Item {} saved", item);
        return item;
    }

    @Override
    public Item update(Item item) {
        log.info("Updating item {}", item);
        getItemOrThrow(item);
        items.put(item.getId(), item);
        log.info("Item {} updated", item);
        return item;
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        log.info("Finding item by id: {}", id);
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findItemsByOwnerId(Long id) {
        log.info("Finding items for owner id: {}", id);
        return items.values()
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Searching items with text: {}", text);
        String lowerCaseText = text.toLowerCase();
        return items.values()
                .stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText)
                        || item.getDescription().toLowerCase().contains(lowerCaseText))
                .collect(Collectors.toList());
    }

    private Item getItemOrThrow(Item item) {
        if (item.getId() == null || !items.containsKey(item.getId())) {
            log.warn("Item {} not found", item);
            throw new NotFoundException("Item with id " + item.getId() + " not found");
        }
        return item;
    }
}
