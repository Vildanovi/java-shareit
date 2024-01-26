package ru.practicum.shareit.request.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constants;
import ru.practicum.shareit.request.dto.ItemRequestNewDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestServiceImpl requestService;

    @PostMapping
    @Operation(summary = "Создать запрос веещии")
    public ItemResponseDto postRequest(@RequestHeader(Constants.USER_ID) int userId,
                                       @Validated(CreatedBy.class) @RequestBody ItemRequestNewDto itemRequestNewDto) {
        ItemRequest itemRequest = RequestMapper.mapRequestDtoToItemRequest(itemRequestNewDto);
        return RequestMapper.mapRequestToResponse(requestService.createRequest(itemRequest, userId));
    }


}
