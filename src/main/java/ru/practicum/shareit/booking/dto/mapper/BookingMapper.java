package ru.practicum.shareit.booking.dto.mapper;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;


public class BookingMapper {
    public static BookingResponseDto toBookingDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toResponseDto(booking.getItem()),
                UserMapper.toResponseDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingResponseDto bookingResponseDto, Item item, User broker) {
        return new Booking(
                bookingResponseDto.getId(),
                bookingResponseDto.getStart(),
                bookingResponseDto.getEnd(),
                item,
                broker,
                bookingResponseDto.getStatus()
        );
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto, Item item, User broker) {
        return new Booking(
                null,
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(),
                item,
                broker,
                BookingStatus.WAITING);
    }
}
