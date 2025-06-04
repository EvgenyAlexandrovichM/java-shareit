package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Creating item: {}", itemDto);
        User owner = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Owner not found with id " + userId));
        itemDto.setOwner(owner);
        Item item = ItemMapper.toItem(itemDto);
        Item createdItem = itemRepository.save(item);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        log.info("Updating item id: {} by user id: {}", itemId, userId);
        Item existingItem = getItemOrThrow(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            log.warn("User: {} is not the owner of the item: {}", userId, itemId);
            throw new ForbiddenException("Only owner can update the item");
        }
        if (itemUpdateDto.getName() != null) {
            existingItem.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            existingItem.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            existingItem.setAvailable(itemUpdateDto.getAvailable());
        }
        itemRepository.update(existingItem);
        return ItemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItemById(Long id) {
        log.info("Getting item by id: {}", id);
        Item item = getItemOrThrow(id);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwnerId(Long id) {
        log.info("Getting items for owner id: {}", id);
        List<Item> items = itemRepository.findAll();
        if (items == null) {
            throw new NotFoundException("Item not found with id " + id);
        }
        return items
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(id))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Getting items with text: {}", text);
        if (text == null || text.isBlank()) {
            log.warn("Empty search query provided, returning empty list");
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.findAll();
        if (items == null) {
            log.warn("Item: {} not found", text);
            throw new NotFoundException("Item not found with text " + text);
        }
        return items
                .stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item getItemOrThrow(Long id) {
        return itemRepository.findItemById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }
}
