package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto create(Long userId, ItemRequestCreateDto dto);

    List<ItemRequestWithItemsDto> getRequestsByUserId(Long id);

    ItemRequestWithItemsDto getRequestById(Long id);

    List<ItemRequestWithItemsDto> getAll(Long userId, Integer from, Integer size);

}