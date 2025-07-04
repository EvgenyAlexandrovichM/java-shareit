package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.BookingState;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid BookingCreateDto bookingCreateDto) {

        log.info("Gateway: create booking userId: {}", userId);
        return client.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {

        log.info("Gateway: approve bookingId: {} by ownerId: {}, approved: {}",
                bookingId, ownerId, approved);
        return client.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {

        log.info("Gateway: get booking by id: {} for userId: {}", bookingId, userId);
        return client.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Gateway: list bookings for userId: {}, state: {}, page: {}, size: {}",
                userId, state, page, size);
        return client.getBookingsByUser(userId, state, page, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {

        log.info("Gateway: list bookings for ownerId: {}, state: {}", ownerId, state);
        return client.getBookingsByOwner(ownerId, state);
    }
}