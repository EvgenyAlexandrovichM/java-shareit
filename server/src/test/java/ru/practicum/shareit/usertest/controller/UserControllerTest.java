package ru.practicum.shareit.usertest.controller;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_success() throws Exception {
        UserCreateDto input = new UserCreateDto("Ivan", "ivan@yandex.ru");
        UserResponseDto output = new UserResponseDto(1L, "Ivan", "ivan@yandex.ru");

        given(service.create(any(UserCreateDto.class)))
                .willReturn(output);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@yandex.ru"));
    }

    @Test
    void createUser_throwConflict() throws Exception {
        UserCreateDto input = new UserCreateDto("Ivan", "ivan@yandex.ru");

        given(service.create(any(UserCreateDto.class)))
                .willThrow(new ConflictException("Email already exists"));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict: Email already exists"));
    }

    @Test
    void createUser_throwBadRequest() throws Exception {
        UserCreateDto input = new UserCreateDto("Ivan", "ivan.yandex.ru");

        given(service.create(any(UserCreateDto.class)))
                .willThrow(new BadRequestException("Incorrect email address"));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad request: Incorrect email address"));
    }

    @Test
    void updateUser_success() throws Exception {
        UserUpdateDto update = new UserUpdateDto("Ivan", null);
        UserResponseDto output = new UserResponseDto(1L, "Ivan", "ivan@yandex.ru");

        given(service.update(eq(1L),any(UserUpdateDto.class))).willReturn(output);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@yandex.ru"));
    }

    @Test
    void updateUser_throwNotFound() throws Exception {
        UserUpdateDto update = new UserUpdateDto("Ivan", "ivan@yandex.ru");
        given(service.update(eq(10L), any(UserUpdateDto.class))).willThrow(new NotFoundException("User not found"));

        mockMvc.perform(patch("/users/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found: User not found"));
    }

    @Test
    void updateUser_throwBadRequest() throws Exception {
        UserUpdateDto update = new UserUpdateDto(null, "ivan.yandex.ru");

        given(service.update(eq(1L), any(UserUpdateDto.class)))
                .willThrow(new BadRequestException("Incorrect email address"));

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(update)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad request: Incorrect email address"));
    }

    @Test
    void getUserById_success() throws Exception {
        UserResponseDto output = new UserResponseDto(2L, "Igor", "igor@yandex.ru");

        given(service.getUserById(2L)).willReturn(output);

        mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Igor"))
                .andExpect(jsonPath("$.email").value("igor@yandex.ru"));
    }

    @Test
    void getUserById_throwNotFound() throws Exception {
        given(service.getUserById(10L)).willThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found: User not found"));
    }

    @Test
    void getAllUsers_success() throws Exception {
        List<UserResponseDto> list = List.of(
                new UserResponseDto(3L, "Oleg", "oleg@yandex.ru"),
                new UserResponseDto(4L, "Elena", "elena@yandex.ru")
        );

        given(service.getAll()).willReturn(list);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Oleg"))
                .andExpect(jsonPath("$[1].name").value("Elena"));
    }

    @Test
    void deleteUser_success() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_throwNotFound() throws Exception {
        doThrow(new NotFoundException("Not found"))
                .when(service).delete(eq(10L));

        mockMvc.perform(delete("/users/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found: Not found"));
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
