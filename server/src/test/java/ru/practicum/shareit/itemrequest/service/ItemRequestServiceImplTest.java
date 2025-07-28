package ru.practicum.shareit.itemrequest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestMapper mapper;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private final Long userId = 1L;
    private final Long requestId = 10L;

    private User user;
    private ItemRequestCreateDto createDto;

    @BeforeEach
    void setUp() {
        user = new User(userId, "Ivan", "ivan@yandex.ru");
        createDto = new ItemRequestCreateDto("Need a stick");
    }

    @Test
    void createRequest_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ItemRequest savedRequest = ItemRequest.builder()
                .id(requestId)
                .description(createDto.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedRequest);

        ItemRequestResponseDto expectedDto = new ItemRequestResponseDto(
                requestId,
                createDto.getDescription(),
                savedRequest.getCreated()
        );
        when(mapper.toItemRequestResponseDto(savedRequest)).thenReturn(expectedDto);

        ItemRequestResponseDto result = service.create(userId, createDto);

        assertThat(result).isEqualTo(expectedDto);

        ArgumentCaptor<ItemRequest> captor = ArgumentCaptor.forClass(ItemRequest.class);
        verify(itemRequestRepository).save(captor.capture());
        ItemRequest toSave = captor.getValue();
        assertThat(toSave.getId()).isNull();
        assertThat(toSave.getDescription()).isEqualTo(createDto.getDescription());
        assertThat(toSave.getRequester()).isEqualTo(user);
        assertThat(toSave.getCreated()).isNotNull();
    }

    @Test
    void createRequest_throwNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.create(userId, createDto));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getRequestsByUserId_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ItemRequest request = ItemRequest.builder()
                .id(requestId)
                .description("Need a hockey stick")
                .requester(user)
                .created(LocalDateTime.now().minusDays(1))
                .build();
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId)).thenReturn(List.of(request));

        Item item = new Item();
        when(itemRepository.findByItemRequestId(requestId)).thenReturn(List.of(item));

        ItemRequestWithItemsDto dto = ItemRequestWithItemsDto.builder()
                .id(requestId)
                .description(request.getDescription())
                .created(request.getCreated())
                .items(List.of())
                .build();
        when(mapper.toItemRequestWithItemsDto(request, List.of(item))).thenReturn(dto);

        List<ItemRequestWithItemsDto> result = service.getRequestsByUserId(userId);

        assertThat(result).containsExactly(dto);
    }

    @Test
    void getRequestsByUserId_throwNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getRequestsByUserId(userId));

        verify(itemRequestRepository, never()).findByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getRequestById_success() {
        ItemRequest request = ItemRequest.builder()
                .id(requestId)
                .description("Need a hockey stick")
                .requester(user)
                .created(LocalDateTime.now().minusHours(2))
                .build();
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        Item item = new Item();
        when(itemRepository.findByItemRequestId(requestId)).thenReturn(List.of(item));

        ItemRequestWithItemsDto dto = ItemRequestWithItemsDto.builder()
                .id(requestId)
                .description(request.getDescription())
                .created(request.getCreated())
                .items(emptyList())
                .build();
        when(mapper.toItemRequestWithItemsDto(request, List.of(item))).thenReturn(dto);
        ItemRequestWithItemsDto result = service.getRequestById(requestId);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getRequestById_thrownNotFound() {
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.getRequestById(requestId));

        verify(itemRepository, never()).findByItemRequestId(anyLong());
    }

    @Test
    void getAll_success() {
        int from = 5;
        int size = 3;
        ItemRequest request1 = ItemRequest.builder().id(10L).build();
        ItemRequest request2 = ItemRequest.builder().id(11L).build();
        Page<ItemRequest> page = new PageImpl<>(List.of(request1, request2));
        when(itemRequestRepository.findByRequesterIdNot(userId, PageRequest.of(from / size, size)))
                .thenReturn(page);

        when(itemRepository.findByItemRequestId(10L)).thenReturn(emptyList());
        when(itemRepository.findByItemRequestId(11L)).thenReturn(emptyList());

        ItemRequestWithItemsDto dto1 = ItemRequestWithItemsDto.builder().id(10L).items(emptyList()).build();
        ItemRequestWithItemsDto dto2 = ItemRequestWithItemsDto.builder().id(11L).items(emptyList()).build();
        when(mapper.toItemRequestWithItemsDto(eq(request1), anyList())).thenReturn(dto1);
        when(mapper.toItemRequestWithItemsDto(eq(request2), anyList())).thenReturn(dto2);

        List<ItemRequestWithItemsDto> result = service.getAll(userId, from, size);

        assertThat(result).containsExactly(dto1, dto2);
    }

    @Test
    void getAll_returnEmptyList() {
        int from = 5;
        int size = 3;
        when(itemRequestRepository.findByRequesterIdNot(userId, PageRequest.of(from / size, size)))
                .thenReturn(Page.empty());

        List<ItemRequestWithItemsDto> result = service.getAll(userId, from, size);

        assertThat(result).isEmpty();
    }

}
