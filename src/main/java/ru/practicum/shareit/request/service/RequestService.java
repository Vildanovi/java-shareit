package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

public interface RequestService {

    ItemRequest createRequest(ItemRequest itemRequest, int userId);
}
