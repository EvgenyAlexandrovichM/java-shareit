package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestResponseDto create(Long userId, ItemRequestCreateDto dto) {
        User requester = getUserOrThrow(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .description(dto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestResponseDto(savedRequest);
    }

    @Override
    public List<ItemRequestWithItemsDto> getRequestsByUserId(Long id) {
        getUserOrThrow(id);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(id);
        return requests.stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.findByItemRequestId(itemRequest.getId());
                    return itemRequestMapper.toItemRequestWithItemsDto(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithItemsDto getRequestById(Long id) {
        ItemRequest itemRequest = getItemRequestOrThrow(id);

        List<Item> items = itemRepository.findByItemRequestId(itemRequest.getId());

        return itemRequestMapper.toItemRequestWithItemsDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestWithItemsDto> getAll(Long userId, Integer from, Integer size) {
        org.springframework.data.domain.Pageable pageable = PageRequest.of(from / size, size);
        return itemRequestRepository.findByRequesterIdNot(userId, pageable)
                .getContent()
                .stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.findByItemRequestId(itemRequest.getId());
                    return itemRequestMapper.toItemRequestWithItemsDto(itemRequest, items);
                })
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }

    private ItemRequest getItemRequestOrThrow(Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found with id " + id));
    }
}
