package ru.practicum.shareit.request.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequestDto created = requestService.create(userId, itemRequestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestWithResponsesDto>> getRequestsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequestWithResponsesDto> requests = requestService.getRequestsByUserId(userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/{all}")
    public ResponseEntity<List<ItemRequestWithResponsesDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequestWithResponsesDto> requests = requestService.getAll(userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestWithResponsesDto> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable Long requestId) {
        ItemRequestWithResponsesDto request = requestService.getRequestById(userId, requestId);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }
}
