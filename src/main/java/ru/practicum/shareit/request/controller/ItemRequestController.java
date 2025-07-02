package ru.practicum.shareit.request.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestResponseDto> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                @Valid @RequestBody ItemRequestCreateDto dto) {

        ItemRequestResponseDto created = requestService.create(userId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestWithItemsDto>> getRequestsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequestWithItemsDto> requests = requestService.getRequestsByUserId(userId);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestWithItemsDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue =  "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue =  "10") @Positive Integer size) {

        List<ItemRequestWithItemsDto> requests = requestService.getAll(userId, from, size);
        return new ResponseEntity<>(requests, HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestWithItemsDto> getRequestById(@PathVariable Long requestId) {
        ItemRequestWithItemsDto request = requestService.getRequestById(requestId);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }
}
