package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BookingDto {

    private Long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private Status status;
}
