package ru.practicum.shareit.bookingtest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService service;

    private static final String HEADER = "X-Sharer-User-Id";

    private BookingCreateDto create;
    private BookingResponseDto response;

    @BeforeEach
    void setUp() {
        create = new BookingCreateDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L
        );

        response = new BookingResponseDto(
                100L,
                create.getStart(),
                create.getEnd(),
                new ItemResponseDto(1L,
                        "Hockey stick",
                        "Awesome",
                        2L,
                        true,
                        null,
                        null,
                        emptyList(),
                        emptyList()),
                new UserResponseDto(3L,
                        "Ivan",
                        "ivan@yandex.ru"),
                BookingStatus.WAITING
        );
    }


    @Test
    void createBooking_success() throws  Exception {
        given(service.createBooking(eq(3L), any(BookingCreateDto.class))).willReturn(response);

        mockMvc.perform(post("/bookings")
                .header(HEADER, 3L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void createBooking_throwConflict() throws Exception {
        given(service.createBooking(anyLong(), any(BookingCreateDto.class)))
                .willThrow(new ConflictException("Item not available"));

        mockMvc.perform(post("/bookings")
                .header(HEADER, 3L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(create)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict: Item not available"));
    }

    @Test
    void createBooking_throwBadRequest() throws Exception {
        given(service.createBooking(anyLong(), any(BookingCreateDto.class)))
                .willThrow(new BadRequestException("End before start"));

        mockMvc.perform(post("/bookings")
                .header(HEADER, 3L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(create)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad request: End before start"));
    }

    @Test
    void approveBooking_success() throws Exception {
        given(service.approveBooking(2L, 100L, true)).willReturn(response);

        mockMvc.perform(patch("/bookings/100")
                .header(HEADER, 2L)
                .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void approveBooking_throwNotFound() throws Exception {
       doThrow(new NotFoundException("Booking not found"))
               .when(service)
               .approveBooking(anyLong(), anyLong(), anyBoolean());

       mockMvc.perform(patch("/bookings/5")
               .header(HEADER, 2L)
               .param("approved", "false"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.error").value("Not found: Booking not found"));
    }

    @Test
    void getBookingById_success() throws Exception {
        given(service.getBookingById(3L, 100L)).willReturn(response);

        mockMvc.perform(get("/bookings/100")
                .header(HEADER, 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void getBookingById_throwForbidden() throws Exception {
        doThrow(new ForbiddenException("Access denied"))
                .when(service)
                .getBookingById(anyLong(), anyLong());

        mockMvc.perform(get("/bookings/100")
                .header(HEADER, 3L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied: Access denied"));
    }

    @Test
    void getBookingsByUser_success() throws Exception {
        given(service.getBookingsByUser(3L, BookingState.ALL, 0 , 10))
                .willReturn(List.of(response));

        mockMvc.perform(get("/bookings")
                .header(HEADER, 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100));
    }

    @Test
    void getBookingsByUser_throwNotFound() throws Exception {
        given(service.getBookingsByUser(eq(99L), eq(BookingState.ALL), eq(0), eq(10)))
                .willThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/bookings")
                .header(HEADER, 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found: User not found"));
    }

    @Test
    void getBookingsByOwner_success() throws Exception {
        given(service.getBookingsByOwner(2L, BookingState.ALL)).willReturn(List.of(response));

        mockMvc.perform(get("/bookings/owner")
                .header(HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getBookingsByOwner_throwNotFound() throws Exception {
        given(service.getBookingsByOwner(eq(99L), eq(BookingState.ALL)))
                .willThrow(new NotFoundException("Owner not found"));

        mockMvc.perform(get("/bookings/owner")
                .header(HEADER, 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found: Owner not found"));
    }


    private String toJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }
}
