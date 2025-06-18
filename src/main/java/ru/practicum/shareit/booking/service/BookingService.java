package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;


import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(Long userId, BookingCreateDto bookingCreateDto);

    BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getBookingsByUser(Long userId, BookingState state,  int page, int size);

    List<BookingResponseDto> getBookingsByOwner(Long ownerId, BookingState state);
}
