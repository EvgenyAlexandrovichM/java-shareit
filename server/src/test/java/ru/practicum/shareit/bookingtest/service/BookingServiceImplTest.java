package ru.practicum.shareit.bookingtest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl service;

    private User booker;
    private User owner;
    private Item item;
    private BookingCreateDto create;
    private Booking booking;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "Booker", "booker@yandex.ru");
        owner = new User(2L, "owner", "owner@yandex.ru");
        item = new Item(10L,
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                owner,
                true,
                null,
                null,
                null);
        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        create = new BookingCreateDto(start, end, item.getId());
        booking = new Booking(100L, start, end, item, booker, BookingStatus.WAITING);
    }

    @Test
    void createBooking_success() {
        optionalBooker();
        optionalItem();
        willAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(booking.getId());
            return b;
        }).given(bookingRepository).save(any(Booking.class));

        BookingResponseDto result = service.createBooking(booker.getId(), create);

        assertEquals(booking.getId(), result.getId());
        assertEquals(BookingStatus.WAITING, result.getStatus());
        then(bookingRepository).should().save(any(Booking.class));
    }

    @Test
    void createBooking_throwBadRequest_endBeforeStart() {
        BookingCreateDto badDto = new BookingCreateDto(end, start, item.getId());

        assertThrows(BadRequestException.class,
                () -> service.createBooking(booker.getId(), badDto));
        verifyNoInteractions(userRepository, itemRepository, bookingRepository);
    }

    @Test
    void createBooking_throwBadRequest_itemNotAvailable() {
        item.setAvailable(false);
        optionalBooker();
        optionalItem();

        assertThrows(BadRequestException.class,
                () -> service.createBooking(booker.getId(), create));
        then(bookingRepository).should(never()).save(any());
    }

    @Test
    void createBooking_throwForbidden_userIsOwner() {
        optionalOwner();
        optionalItem();
        BookingCreateDto ownerDto = new BookingCreateDto(start, end, item.getId());

        assertThrows(ForbiddenException.class,
                () -> service.createBooking(owner.getId(), ownerDto));
        then(bookingRepository).should(never()).save(any());
    }

    @Test
    void createBooking_throwNotFound_userNotFound() {
        emptyBooker();

        assertThrows(NotFoundException.class,
                () -> service.createBooking(booker.getId(), create));

        verifyNoInteractions(itemRepository, bookingRepository);
    }

    @Test
    void createBooking_throwNotFound_itemNotFound() {
        optionalBooker();
        emptyItem();

        assertThrows(NotFoundException.class,
                () -> service.createBooking(booker.getId(), create));

        then(bookingRepository).should(never()).save(any());
    }

    @Test
    void approveBooking_success_approved() {
        optionalBooking();
        willAnswer(inv -> inv.getArgument(0)).given(bookingRepository).save(any(Booking.class));

        BookingResponseDto result = service.approveBooking(owner.getId(), booking.getId(), true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        then(bookingRepository).should().save(booking);
    }

    @Test
    void approvedBooking_success_rejected() {
        optionalBooking();
        willAnswer(inv -> inv.getArgument(0)).given(bookingRepository).save(any(Booking.class));

        BookingResponseDto result = service.approveBooking(owner.getId(), booking.getId(), false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void approvedBooking_throwForbidden_notOwner() {
        optionalBooking();

        assertThrows(ForbiddenException.class,
                () -> service.approveBooking(booker.getId(), booking.getId(), true));
    }

    @Test
    void approvedBooking_throwNotFound_bookingNotFound() {
        emptyBooking();

        assertThrows(NotFoundException.class,
                () -> service.approveBooking(owner.getId(), booking.getId(), true));

        then(bookingRepository).should(never()).save(any());
    }

    @Test
    void getBookingById_successBooker() {
        optionalBooking();

        BookingResponseDto result = service.getBookingById(booker.getId(), booking.getId());
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingById_successOwner() {
        optionalBooking();

        BookingResponseDto result = service.getBookingById(owner.getId(), booking.getId());
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void getBookingById_throwForbidden() {
        User other = new User(3L, "Other", "other@yandex.ru");
        optionalBooking();
        assertThrows(ForbiddenException.class,
                () -> service.getBookingById(other.getId(), booking.getId()));
    }

    @Test
    void getBookingById_throwNotFound() {
        emptyBooking();
        assertThrows(NotFoundException.class,
                () -> service.getBookingById(booker.getId(), booking.getId()));
    }

    @Test
    void getBookingsByUser_success() {
        optionalBooker();
        Page<Booking> page = new PageImpl<>(List.of(booking));
        given(bookingRepository.findBookingsByBookerId(eq(booker.getId()), any(PageRequest.class)))
                .willReturn(page);

        List<BookingResponseDto> result = service.getBookingsByUser(booker.getId(), BookingState.ALL, 0, 10);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.getFirst().getId());
    }

    @Test
    void getBookingsByUser_throwNotFound_bookerNotFound() {
        emptyBooker();
        assertThrows(NotFoundException.class,
                () -> service.getBookingsByUser(booker.getId(), BookingState.ALL, 0, 10));
    }

    @Test
    void getBookingsByOwner_success() {
        optionalOwner();
        given(bookingRepository.findBookingsByOwner(owner.getId())).willReturn(List.of(booking));
        List<BookingResponseDto> result = service.getBookingsByOwner(owner.getId(), BookingState.ALL);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.getFirst().getId());
    }

    @Test
    void getBookingsByOwner_throwNotFound_ownerNotFound() {
        emptyOwner();
        assertThrows(NotFoundException.class,
                () -> service.getBookingsByOwner(owner.getId(), BookingState.ALL));
    }

    private void optionalBooker() {
        given(userRepository.findById(booker.getId())).willReturn(Optional.of(booker));
    }

    private void optionalOwner() {
        given(userRepository.findById(owner.getId())).willReturn(Optional.of(owner));
    }

    private void optionalItem() {
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
    }

    private void emptyBooker() {
        given(userRepository.findById(booker.getId())).willReturn(Optional.empty());
    }

    private void emptyItem() {
        given(itemRepository.findById(item.getId())).willReturn(Optional.empty());
    }

    private void optionalBooking() {
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.of(booking));
    }

    private void emptyBooking() {
        given(bookingRepository.findById(booking.getId())).willReturn(Optional.empty());
    }

    private void emptyOwner() {
        given(userRepository.findById(owner.getId())).willReturn(Optional.empty());
    }
}
