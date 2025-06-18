package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findBookingsByBookerId(@Param("bookerId") Long bookerId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookings(@Param("bookerId") Long bookerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookings(@Param("bookerId") Long bookerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookings(@Param("bookerId") Long bookerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :bookerId AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findBookingsByStatus(@Param("bookerId") Long bookerId, @Param("status") BookingStatus status);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findPastBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureBookingsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id = :itemId AND b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP AND b.status = 'APPROVED'")
    List<Booking> findCompletedBookings(@Param("itemId") Long itemId, @Param("userId") Long userId);
}

