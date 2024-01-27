package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequest createRequest(ItemRequest itemRequest, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        itemRequest.setRequestor(userId);
        itemRequest.setCreated(LocalDateTime.now());
        return requestRepository.save(itemRequest);
    }

    @Override
    public List<ItemResponseDto> getAllRequestByOwner(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        List<ItemRequest> requests = requestRepository
                .findAllByRequestor(userId, Sort.by(Sort.Direction.DESC, "created"));
        return putItems(requests);
    }

    @Override
    public List<ItemResponseDto> getAllRequest(int userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        List<ItemRequest> requests = requestRepository.findAllByRequestorNot(userId,
                PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created")));
        return putItems(requests);
    }

    @Override
    public ItemResponseDto getRequestById(int userId, int requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + requestId));
        List<ItemDto> items = itemRepository.findAllByRequest(itemRequest.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
        ItemResponseDto result = RequestMapper.mapRequestToResponse(itemRequest);
        result.setItems(items);
        return result;
    }

    private List<ItemResponseDto> putItems(List<ItemRequest> requests) {
        List<Integer> ids = requests.stream()
                .map(ItemRequest::getId)
                .collect(toList());

        Map<Integer, List<ItemDto>> map = itemRepository
                .findAllByRequestIn(ids)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(groupingBy(ItemDto::getRequestId, toList()));

        List<ItemResponseDto> itemRequests = requests
                .stream()
                .map(RequestMapper::mapRequestToResponse)
                .collect(toList());

        for (ItemResponseDto request : itemRequests) {
            request.setItems(map.getOrDefault(request.getId(), Collections.emptyList()));
        }
        return itemRequests;
    }
}
