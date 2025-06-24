package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;
    private Long lastBooking;
    private Long nextBooking;
    private List<BookingResponseDto> bookings;
    private List<CommentResponseDto> comments;
}
