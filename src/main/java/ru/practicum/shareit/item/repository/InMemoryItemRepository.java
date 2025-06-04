package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
@Qualifier("inMemoryItemRepository")
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
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
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }
}
