package ru.practicum.shareit.item.dto.mapper;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public class ItemMapper {
    public static ItemResponseDto toResponseDto(Item item) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getOwner().getId(),
                item.getAvailable(),
                null,
                null,
                new ArrayList<>()
        );
    }

    public static Item toItem(ItemCreateDto itemCreateDto, User owner) {
        Item item = new Item();
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public static Item toItem(Item item, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        return item;
    }

    public static Item toItem(ItemResponseDto itemResponseDto, User owner) {
        Item item = new Item();
        item.setName(itemResponseDto.getName());
        item.setDescription(itemResponseDto.getDescription());
        item.setAvailable(itemResponseDto.getAvailable());
        item.setOwner(owner);
        return item;
    }
}
