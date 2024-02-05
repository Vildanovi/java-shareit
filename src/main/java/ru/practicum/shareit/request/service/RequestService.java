package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {

    ItemRequest createRequest(ItemRequest itemRequest, int userId);

    List<ItemResponseDto> getAllRequestByOwner(int userId);

    List<ItemResponseDto> getAllRequest(int userId, Integer from, Integer size);

    ItemResponseDto getRequestById(int userId, int requestId);
}
