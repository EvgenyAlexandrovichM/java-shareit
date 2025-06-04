package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Booking {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User broker;
    private Status status;
}
