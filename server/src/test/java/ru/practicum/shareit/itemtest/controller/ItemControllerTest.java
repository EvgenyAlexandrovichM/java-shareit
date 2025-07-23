package ru.practicum.shareit.itemtest.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createItem_success() throws Exception {
        ItemCreateDto create = new ItemCreateDto(
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                true,
                null
        );

        ItemResponseDto output = new ItemResponseDto(
                1L,
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                2L,
                true,
                null,
                null,
                List.of(),
                List.of()
        );

        given(itemService.create(eq(2L),any(ItemCreateDto.class)))
                .willReturn(output);

        mockMvc.perform(post("/items")
                .header(USER_HEADER, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hockey stick"))
                .andExpect(jsonPath("$.available").value(true));

    }

    @Test
    void createItem_throwConflict() throws Exception {
        ItemCreateDto create = new ItemCreateDto(
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                true,
                null
        );

        given(itemService.create(anyLong(), any()))
                .willThrow(new ConflictException("Duplicate"));

        mockMvc.perform(post("/items")
                .header(USER_HEADER, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(create)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict: Duplicate"));

    }

    @Test
    void createItemWithoutOwner_throwAll() throws Exception {
        ItemCreateDto create = new ItemCreateDto(
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                true,
                null
        );

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(create)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateItem_success() throws Exception {
        ItemUpdateDto update = new ItemUpdateDto("Another hockey stick", null, null);
        ItemResponseDto output = new ItemResponseDto(
                1L,
                "Hockey stick", "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                2L,
                true,
                null,
                null,
                List.of(),
                List.of()
        );

        given(itemService.update(eq(2L), eq(1L), any(ItemUpdateDto.class)))
                .willReturn(output);

        mockMvc.perform(patch("/items/1")
                .header(USER_HEADER, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New hockey stick"));
    }

    @Test
    void updateItem_throwForbidden() throws Exception {
        ItemUpdateDto update = new ItemUpdateDto(
                "Hockey stick",
                "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                true
        );
        doThrow(new ForbiddenException("Not owner"))
                .when(itemService).update(eq(2L), eq(1L), any());

        mockMvc.perform(patch("/items/1")
                .header(USER_HEADER, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(update)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied: Not owner"));
    }

    @Test
    void getItemById_success() throws Exception {
        ItemResponseDto output = new ItemResponseDto(
                1L,
                "Hockey stick", "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                2L,
                true,
                null,
                null,
                List.of(),
                List.of()
        );

        given(itemService.getItemById(1L)).willReturn(output);

        mockMvc.perform(get("/items/1")
                .header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ownerId").value(2));

    }

    @Test
    void getItemById_throwNotFound() throws Exception {
        given(itemService.getItemById(1L)).willThrow(new NotFoundException("Not found"));

        mockMvc.perform(get("/items/1")
                .header(USER_HEADER, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found: Not found"));
    }

    @Test
    void getItemByOwnerId_success() throws Exception {
        List<ItemResponseDto> list = List.of(
                new ItemResponseDto(
                        1L,
                        "A",
                        "a",
                        1L,
                        true,
                        null,
                        null,
                        List.of(),
                        List.of()
                ),
                new ItemResponseDto(
                        2L,
                        "B",
                        "b",
                        1L,
                        true,
                        null,
                        null,
                        List.of(),
                        List.of()
                )
        );
        given(itemService.getItemsByOwnerId(1L)).willReturn(list);

        mockMvc.perform(get("/items")
                .header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void searchItem_success() throws Exception {
        List<ItemResponseDto> list = List.of(
                new ItemResponseDto(
                        1L,
                        "Hockey stick", "Stick - CCM Jetspeed Ft5 Pro INT, flex - 65, bend - P90, grip - RHT",
                        2L,
                        true,
                        null,
                        null,
                        List.of(),
                        List.of()
                )
        );
        given(itemService.searchItems("Hockey stick")).willReturn(list);

        mockMvc.perform(get("/items/search")
                        .header(USER_HEADER,2L)
                        .param("text", "Hockey stick"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hockey stick"));
    }

    @Test
    void searchItemsEmpty_success() throws Exception {
        given(itemService.searchItems("")).willReturn(List.of());

        mockMvc.perform(get("/items/search")
                .header(USER_HEADER,1L)
                .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void createCommentForItem_success() throws Exception {
        CommentCreateDto create = new CommentCreateDto("Great stick!");
        CommentResponseDto output = new CommentResponseDto(
                10L, "Great stick!", "Ivan", LocalDateTime.now()
        );
        given(commentService.createComment(2L, 1L, create)).willReturn(output);

        mockMvc.perform(post("/items/1/comment")
                .header(USER_HEADER, 2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.text").value("Great stick!"));
    }

    @Test
    void createCommentForItem_throwBadRequest() throws Exception {
        CommentCreateDto create = new CommentCreateDto("");
        given(commentService.createComment(anyLong(), anyLong(), any()))
                .willThrow(new BadRequestException("Empty comment"));

        mockMvc.perform(post("/items/1/comment")
                .header(USER_HEADER,2L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(create)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad request: Empty comment"));
    }

    @Test
    void getCommentsForItem_success() throws Exception {
        List<CommentResponseDto> list = List.of(
                new CommentResponseDto(10L, "Awesome stick", "Igor", LocalDateTime.now()),
                new CommentResponseDto(11L, "Pretty nice!", "Stepan", LocalDateTime.now())
        );
        given(commentService.getCommentsForItem(1L)).willReturn(list);

        mockMvc.perform(get("/items/1/comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].authorName").value("Igor"));
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
