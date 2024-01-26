package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

@UtilityClass
public class RequestMapper {

    public ItemRequest mapRequestDtoToItemRequest(ItemRequestNewDto itemRequestNewDto) {
        return ItemRequest.builder()
                .description(itemRequestNewDto.getDescription())
                .build();
    }

    public ItemResponseDto mapRequestToResponse(ItemRequest itemRequest) {
        return ItemResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}
