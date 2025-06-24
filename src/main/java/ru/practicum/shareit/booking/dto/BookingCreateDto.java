package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDto {

    @NotNull(message = "Start time cannot be null")
    @Future(message = "Start time cannot be past")
    private LocalDateTime start;

    @NotNull(message = "End time cannot be null")
    @Future(message = "End time cannot be past")
    private LocalDateTime end;

    @NotNull(message = "Item id cannot be null")
    private Long itemId;
}
