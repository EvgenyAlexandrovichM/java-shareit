package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid ItemCreateDto itemCreateDto) {

        log.info("Gateway: POST /items userId: {}, body: {}", userId, itemCreateDto);
        return client.createItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody @Valid ItemUpdateDto itemUpdateDto) {

        log.info("Gateway: PATCH /items/{} userId: {}, body: {}", itemId, userId, itemUpdateDto);
        return client.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId) {

        log.info("Gateway: GET /items/{} userId: {}", itemId, userId);
        return client.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId) {

        log.info("Gateway: GET /items userId: {}", userId);
        return client.getItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam String text) {

        log.info("Gateway: GET /items/search text: {} userId: {}", text, userId);
        return client.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody @Valid CommentCreateDto commentCreateDto) {

        log.info("Gateway: POST /items/{}/comment userId: {}, body: {}", itemId, userId, commentCreateDto);
        return client.createComment(userId, itemId, commentCreateDto);
    }

    @GetMapping("/{itemId}/comment")
    public ResponseEntity<Object> getComments(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId) {

        log.info("Gateway: GET /items/{}/comment", itemId);
        return client.getComments(userId, itemId);
    }
}

