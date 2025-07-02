package ru.practicum.shareit.request.dto.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;


public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemResponseDto toResponseDto(ItemRequest itemRequest, Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }

    public static ItemRequestWithResponsesDto toWithResponsesDto(ItemRequest itemRequest) {
        List<ItemResponseDto> responses = itemRequest.getItems()
                .stream()
                .map(item -> toResponseDto(itemRequest, item))
                .collect(Collectors.toList());

        return ItemRequestWithResponsesDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .responses(responses)
                .build();
    }

}
