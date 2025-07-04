package ru.practicum.shareit.booking.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> createBooking(long userId, BookingCreateDto dto) {
        log.info("Gateway: create booking for userId: {}", userId);
        return post("", userId, dto);
    }

    public ResponseEntity<Object> approveBooking(long ownerId, long bookingId, boolean approved) {
        log.info("Gateway: approve bookingId: {} by ownerId: {}, approved: {}",
                bookingId, ownerId, approved);
        Map<String, Object> params = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", ownerId, params, null);
    }

    public ResponseEntity<Object> getBookingById(long userId, long bookingId) {
        log.info("Gateway: get booking by id: {} for userId: {}", bookingId, userId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsByUser(long userId,
                                                    BookingState state,
                                                    int page,
                                                    int size) {
        log.info("Gateway: list bookings for userId: {}, state: {}, page: {}, size: {}",
                userId, state, page, size);
        Map<String, Object> params = Map.of(
                "state", state.name(),
                "page", page,
                "size", size
        );
        return get("", userId, params);
    }

    public ResponseEntity<Object> getBookingsByOwner(long ownerId, BookingState state) {
        log.info("Gateway: list bookings for ownerId: {}, state: {}", ownerId, state);
        Map<String, Object> params = Map.of("state", state.name());
        // path: /owner?state={state}
        return get("/owner", ownerId, params);
    }
}
