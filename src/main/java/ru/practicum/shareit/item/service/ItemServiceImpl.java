package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public Item create(Item item) {
        log.info("Creating item {}", item);
        return itemRepository.save(item);
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        log.info("Updating item id: {} by user id: {}", itemId, userId);
        Item existingItem = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            log.warn("User {} is not the owner of item {}", userId, itemId);
            throw new ForbiddenException("Only owner can update the item");
        }
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        return itemRepository.update(existingItem);
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        log.info("Getting item by id: {}", id);
        return itemRepository.findItemById(id);
    }

    @Override
    public List<Item> getItemsByOwnerId(Long id) {
      log.info("Getting items for owner id: {}", id);
      return itemRepository.findItemsByOwnerId(id);
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Getting items with text: {}", text);
        return itemRepository.searchItems(text);
    }
}
