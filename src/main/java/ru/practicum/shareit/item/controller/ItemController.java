package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody @Valid ItemDto itemDto) {
        log.info("Request to create item: {}", itemDto);
        if (itemDto.getOwner() == null) {
            itemDto.setOwner(new User(userId, null, null));
        }
        Item item = itemService.create(ItemMapper.toItem(itemDto));
        log.info("Item {} created successfully", itemDto);
        return new ResponseEntity<>(ItemMapper.toItemDto(item), HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody @Valid ItemDto itemDto) {
        log.info("Request to update item id: {} by user id: {}", itemId, userId);
        Item updatedItem = itemService.update(userId, itemId, ItemMapper.toItem(itemDto));
        log.info("Item {} updated successfully", updatedItem);
        return new ResponseEntity<>(ItemMapper.toItemDto(updatedItem), HttpStatus.OK);

    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long itemId) {
        log.info("Request to get item by id: {}", itemId);
        Item item = itemService.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
        log.info("Item {} got successfully", item);
        return new ResponseEntity<>(ItemMapper.toItemDto(item), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request to get items for owner id: {}", userId);
        List<ItemDto> items = itemService.getItemsByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam String text) {
        log.info("Request to search items with text: {}", text);
        List<ItemDto> items = itemService.searchItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
}
