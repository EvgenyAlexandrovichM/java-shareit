package ru.practicum.shareit.booking.dto.mapper;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
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

    public static Booking toBooking(BookingResponseDto bookingDto) {
        User owner = new User();
        owner.setId(bookingDto.getItem().getOwnerId());
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                ItemMapper.toItem(bookingDto.getItem(), owner),
                UserMapper.toUser(bookingDto.getBooker()),
                bookingDto.getStatus()
        );
    }
}
