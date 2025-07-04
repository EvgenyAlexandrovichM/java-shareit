package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestCreateDto {

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    private String description;
}
