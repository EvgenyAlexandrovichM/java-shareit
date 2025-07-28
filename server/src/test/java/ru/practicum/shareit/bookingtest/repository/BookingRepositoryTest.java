package ru.practicum.shareit.bookingtest.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private User otherBooker;
    private Item item;
    private LocalDateTime now;

    private Booking pastApproved;
    private Booking currentWaiting;
    private Booking futureRejected;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        owner = userRepository.save(new User(null, "Owner", "owner@yandex.ru"));
        booker = userRepository.save(new User(null, "Broker", "broker@yandex.ru"));
        otherBooker = userRepository.save(new User(null, "OtherBroker", "otherbroker@yandex.ru"));

        item = itemRepository.save(new Item(
                null,
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                owner,
                true,
                null,
                null,
                null
        ));

        pastApproved = bookingRepository.save(new Booking(
                null,
                now.minusDays(2),
                now.minusDays(1),
                item,
                booker,
                BookingStatus.APPROVED
        ));
        currentWaiting = bookingRepository.save(new Booking(
                null,
                now.minusHours(1),
                now.plusHours(1),
                item,
                booker,
                BookingStatus.WAITING
        ));
        futureRejected = bookingRepository.save(new Booking(
                null,
                now.plusDays(1),
                now.plusDays(2),
                item,
                booker,
                BookingStatus.REJECTED
        ));
    }

    @AfterEach
    void clear() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findPastBookings_success() {
        List<Booking> past = bookingRepository.findPastBookings(booker.getId());
        assertEquals(1, past.size());
        assertEquals(pastApproved.getId(), past.getFirst().getId());
    }

    @Test
    void findPastBookings_emptyList() {
        List<Booking> past = bookingRepository.findPastBookings(otherBooker.getId());
        assertTrue(past.isEmpty());
    }

    @Test
    void findCurrentBookings_success() {
        List<Booking> current = bookingRepository.findCurrentBookings(booker.getId());
        assertEquals(1, current.size());
        assertEquals(currentWaiting.getId(), current.getFirst().getId());
    }

    @Test
    void findCurrentBookings_emptyList() {
        List<Booking> current = bookingRepository.findCurrentBookings(otherBooker.getId());
        assertTrue(current.isEmpty());
    }

    @Test
    void findFutureBookings_success() {
        List<Booking> future = bookingRepository.findFutureBookings(booker.getId());
        assertEquals(1, future.size());
        assertEquals(futureRejected.getId(), future.getFirst().getId());
    }

    @Test
    void findFutureBookings_emptyList() {
        List<Booking> future = bookingRepository.findFutureBookings(otherBooker.getId());
        assertTrue(future.isEmpty());
    }

    @Test
    void findBookingsByStatus_success() {
        List<Booking> waiting = bookingRepository.findBookingsByStatus(booker.getId(), BookingStatus.WAITING);
        assertEquals(1, waiting.size());
        assertEquals(currentWaiting.getId(), waiting.getFirst().getId());
    }

    @Test
    void findBookingsByStatus_emptyList() {
        List<Booking> canceled = bookingRepository.findBookingsByStatus(booker.getId(), BookingStatus.CANCELED);
        assertTrue(canceled.isEmpty());
    }

    @Test
    void findBookingsByBookerId_withPaginationAndSorting() {
        Page<Booking> page = bookingRepository.findBookingsByBookerId(
                booker.getId(),
                PageRequest.of(0, 2)
        );
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getContent().size());
        assertEquals(futureRejected.getId(), page.getContent().getFirst().getId());
        assertEquals(currentWaiting.getId(), page.getContent().get(1).getId());
    }

    @Test
    void findBookingsByBookerId_emptyPage() {
        Page<Booking> page = bookingRepository.findBookingsByBookerId(
                booker.getId(),
                PageRequest.of(5,2)
        );
        assertTrue(page.getContent().isEmpty());
    }

    @Test
    void findCompletedBookings_success() {
        List<Booking> completed = bookingRepository.findCompletedBookings(item.getId(), booker.getId());
        assertEquals(1, completed.size());
        assertEquals(pastApproved.getId(), completed.getFirst().getId());
    }

    @Test
    void findCompletedBookings_emptyList() {
        List<Booking> completed = bookingRepository.findCompletedBookings(item.getId(), otherBooker.getId());
        assertTrue(completed.isEmpty());
    }

    @Test
    void findApprovedByItemIdsOrderByStartAsc_success() {
        List<Booking> approved = bookingRepository.findApprovedByItemIdsOrderByStartAsc(List.of(item.getId()));
        assertEquals(1, approved.size());
        assertEquals(pastApproved.getId(), approved.getFirst().getId());
    }

    @Test
    void findApprovedByItemIdsOrderByStartAsc_emptyList() {
        List<Booking> approved = bookingRepository.findApprovedByItemIdsOrderByStartAsc(List.of(99L));
        assertTrue(approved.isEmpty());
    }
}
