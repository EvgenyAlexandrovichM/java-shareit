package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;



@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestBody @Valid ItemCreateDto itemCreateDto) {
        log.info("Request to create item: {}", itemCreateDto);
        ItemResponseDto item = itemService.create(userId, itemCreateDto);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createCommentForItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Request to create comment for item id {} by user {}", itemId, userId);
        CommentResponseDto commentDto = commentService.createComment(userId, itemId, commentCreateDto);
        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long itemId,
                                                      @RequestBody @Valid ItemUpdateDto itemUpdateDto) {
        log.info("Request to update item id: {} by user id: {}", itemId, userId);
        ItemResponseDto item = itemService.update(userId, itemId, itemUpdateDto);
        return new ResponseEntity<>(item, HttpStatus.OK);

    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long itemId) {
        log.info("Request to get item by id: {}", itemId);
        ItemResponseDto item = itemService.getItemById(itemId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request to get items for owner id: {}", userId);
        List<ItemResponseDto> items = itemService.getItemsByOwnerId(userId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                             @RequestParam String text) {
        log.info("Request to search items with text: {}", text);
        List<ItemResponseDto> items = itemService.searchItems(text);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/{itemId}/comment")
    public ResponseEntity<List<CommentResponseDto>> getCommentsForItem(
            @PathVariable Long itemId) {
        log.info("Request to get comments for item id: {}", itemId);
        List<CommentResponseDto> commentDtos = commentService.getCommentsForItem(itemId);
        return new ResponseEntity<>(commentDtos, HttpStatus.OK);
    }
}
