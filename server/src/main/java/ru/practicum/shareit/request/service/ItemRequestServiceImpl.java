package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(dto, requester);
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestResponseDto(savedRequest);
    }

    @Override
    public List<ItemRequestWithItemsDto> getRequestsByUserId(Long id) {
        getUserOrThrow(id);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(id);
        return enrichWithItems(requests);
    }

    @Override
    public ItemRequestWithItemsDto getRequestById(Long id) {
        ItemRequest itemRequest = getItemRequestOrThrow(id);

        List<Item> items = itemRepository.findByItemRequestId(itemRequest.getId());

        return itemRequestMapper.toItemRequestWithItemsDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestWithItemsDto> getAll(Long userId, Integer from, Integer size) {
        getUserOrThrow(userId);

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNot(userId, pageable).getContent();
        return enrichWithItems(requests);
    }

    private List<ItemRequestWithItemsDto> enrichWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return  Collections.emptyList();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByItemRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));

        return requests.stream()
                .map(req -> {
                    List<Item> it = itemsByRequestId.getOrDefault(req.getId(), Collections.emptyList());
                    return itemRequestMapper.toItemRequestWithItemsDto(req, it);
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
