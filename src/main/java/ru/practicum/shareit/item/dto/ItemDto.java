package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    private User owner;
    @NotNull(message = "Available cannot be null")
    private Boolean available;
}
