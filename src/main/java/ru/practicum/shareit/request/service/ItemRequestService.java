package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto dto);

    List<ItemRequestWithResponsesDto> getRequestsByUserId(Long id);

    ItemRequestWithResponsesDto getRequestById(Long userId, Long requestId);

    List<ItemRequestWithResponsesDto> getAll(Long userId);

}