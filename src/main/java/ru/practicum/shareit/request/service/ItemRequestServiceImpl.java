package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestWithResponsesDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        User user = getUserOrThrow(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .description(dto.getDescription())
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestWithResponsesDto> getRequestsByUserId(Long id) {
        getUserOrThrow(id);
        List<ItemRequest> requests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(id);
        return requests.stream()
                .map(ItemRequestMapper::toWithResponsesDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithResponsesDto getRequestById(Long userId, Long requestId) {
        getUserOrThrow(userId);
        ItemRequest itemRequest = getItemRequestOrThrow(requestId);
        return ItemRequestMapper.toWithResponsesDto(itemRequest);
    }

    @Override
    public List<ItemRequestWithResponsesDto> getAll(Long userId) {
        getUserOrThrow(userId);
       List<ItemRequest> requests = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId);

       return requests.stream()
               .map(ItemRequestMapper::toWithResponsesDto)
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
