package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;



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
        ItemDto item = itemService.create(userId, itemDto);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody @Valid ItemUpdateDto itemUpdateDto) {
        log.info("Request to update item id: {} by user id: {}", itemId, userId);
        ItemDto item = itemService.update(userId, itemId, itemUpdateDto);
        return new ResponseEntity<>(item, HttpStatus.OK);

    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long itemId) {
        log.info("Request to get item by id: {}", itemId);
        ItemDto item = itemService.getItemById(itemId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request to get items for owner id: {}", userId);
        List<ItemDto> items = itemService.getItemsByOwnerId(userId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam String text) {
        log.info("Request to search items with text: {}", text);
        List<ItemDto> items = itemService.searchItems(text);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }
}
