package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {

    CommentResponseDto createComment(Long userId, Long itemId, CommentCreateDto dto);

    List<CommentResponseDto> getCommentsForItem(Long itemId);
}

