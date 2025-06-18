package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public BookingResponseDto createBooking(Long userId, BookingCreateDto bookingCreateDto) {
        User booker = getUserOrThrow(userId);
        Item item = getItemOrThrow(bookingCreateDto.getItemId());

        if (!item.getAvailable()) {
            throw new BadRequestException("Item " + item.getId() + " is not available");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("User " + userId + " is owner of item " + item.getId());
        }
        Booking booking = new Booking();
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));

    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = getBookingOrThrow(bookingId);
        if ((booking.getStatus() != BookingStatus.WAITING)) {
            throw new ConflictException("Booking " + bookingId + " is not in waiting state");
        }

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("User " + userId + " is not the owner of item " + booking.getItem().getId());
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to see booking details");
        }
        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingResponseDto> getBookingsByUser(Long userId, BookingState state, int page, int size) {
        PageRequest pageR = PageRequest.of(page, size);
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findCurrentBookings(userId);
            case PAST -> bookingRepository.findPastBookings(userId);
            case FUTURE -> bookingRepository.findFutureBookings(userId);
            case WAITING -> bookingRepository.findBookingsByStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findBookingsByStatus(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findBookingsByBookerId(userId, pageR).getContent();

        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, BookingState state) {
        getUserOrThrow(ownerId);
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findCurrentBookingsByOwner(ownerId);
            case PAST -> bookingRepository.findPastBookingsByOwner(ownerId);
            case FUTURE -> bookingRepository.findFutureBookingsByOwner(ownerId);
            case WAITING -> bookingRepository.findBookingsByOwnerAndStatus(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findBookingsByOwnerAndStatus(ownerId, BookingStatus.REJECTED);
            default -> bookingRepository.findBookingsByOwner(ownerId);
        };
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Booking getBookingOrThrow(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found with id " + id));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }

    private Item getItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found with id " + id));
    }
}
