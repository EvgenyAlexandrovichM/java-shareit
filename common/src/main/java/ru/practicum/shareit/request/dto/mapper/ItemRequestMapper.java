package ru.practicum.shareit.request.dto.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemRequestItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapperconfig.MapStructConfig;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(config = MapStructConfig.class, uses = {ItemMapper.class})
public interface ItemRequestMapper {

    ItemRequestResponseDto toItemRequestResponseDto(ItemRequest request);

    @Mapping(
            target = "items",
            source = "items",
            qualifiedByName = "toItemRequestItemDto"
    )
    ItemRequestWithItemsDto toItemRequestWithItemsDto(
            ItemRequest request,
            List<Item> items
    );

    @Named("toItemRequestItemDto")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "owner.id", target = "ownerId")
    ItemRequestItemDto toItemRequestItemDto(Item item);


    @Mapping(target = "id", ignore = true)
    @Mapping(source = "dto.description", target = "description")
    @Mapping(source = "requester", target = "requester")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    ItemRequest toItemRequest(ItemRequestCreateDto dto, User requester);
}