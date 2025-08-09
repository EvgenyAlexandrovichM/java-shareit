package ru.practicum.shareit.itemrequest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    private static final Long USER_ID = 1L;
    private static final Long REQUEST_ID = 10L;
    private static final LocalDateTime CREATED = LocalDateTime.of(2025, 7, 28, 5, 27);

    @Test
    void createRequest_success() throws Exception {
        ItemRequestCreateDto input = new ItemRequestCreateDto("Need a stick");
        ItemRequestResponseDto output = new ItemRequestResponseDto(REQUEST_ID, "Need a stick", CREATED);

        given(service.create(eq(USER_ID), any(ItemRequestCreateDto.class))).willReturn(output);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value("Need a stick"))
                .andExpect(jsonPath("$.created").value("2025-07-28T05:27:00"));
    }

    @Test
    void createRequest_throwUserNotFound() throws Exception {
        ItemRequestCreateDto input = new ItemRequestCreateDto("Need a stick");
        given(service.create(eq(USER_ID), any(ItemRequestCreateDto.class)))
                .willThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(input)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found: User not found"));
    }

    @Test
    void getRequestsByUserId_success() throws Exception {
        ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto(
                REQUEST_ID,
                "Need a hockey stick",
                CREATED, Collections.emptyList());
        given(service.getRequestsByUserId(USER_ID)).willReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID))
                .andExpect(jsonPath("$[0].description").value("Need a hockey stick"))
                .andExpect(jsonPath("$[0].created").value("2025-07-28T05:27:00"))
                .andExpect(jsonPath("$[0].items").isArray());
    }

    @Test
    void getRequestsByUserId_throwNotFound() throws Exception {
        given(service.getRequestsByUserId(USER_ID)).willThrow(new NotFoundException("User not found"));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found: User not found"));
    }

    @Test
    void getAllRequests_success() throws Exception {
        ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto(REQUEST_ID,
                "Need a hockey stick",
                CREATED,
                Collections.emptyList());
        given(service.getAll(USER_ID, 0, 10)).willReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID))
                .andExpect(jsonPath("$[0].description").value("Need a hockey stick"));
    }

//    @Test
//    void getAllRequests_throwBadRequest() throws Exception {
//        mockMvc.perform(get("/requests/all")
//                        .header("X-Sharer-User-Id", USER_ID)
//                        .param("from", "-1")
//                        .param("size", "5"))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void getAllRequests_throwNotFound() throws Exception {
        given(service.getAll(USER_ID, 0, 10)).willThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found: User not found"));
    }

    @Test
    void getRequestById_success() throws Exception {
        ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto(REQUEST_ID,
                "Need a hockey stick",
                CREATED,
                Collections.emptyList());
        given(service.getAll(USER_ID, 0, 10)).willReturn(List.of(dto));
        given(service.getRequestById(REQUEST_ID)).willReturn(dto);

        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value("Need a hockey stick"))
                .andExpect(jsonPath("$.created").value("2025-07-28T05:27:00"));
    }

    @Test
    void getRequestById_throwNotFound() throws Exception {
        given(service.getRequestById(REQUEST_ID)).willThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/{requestId}", REQUEST_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found: Request not found"));
    }

    private String toJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }
}
