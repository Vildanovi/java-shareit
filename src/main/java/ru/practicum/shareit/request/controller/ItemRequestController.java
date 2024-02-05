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

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final RequestServiceImpl requestService;

    @PostMapping
    @Operation(summary = "Создать запрос веещии")
    public ItemResponseDto postRequest(@RequestHeader(Constants.USER_ID) int userId,
                                       @Validated(CreatedBy.class) @RequestBody ItemRequestNewDto itemRequestNewDto) {
        ItemRequest itemRequest = RequestMapper.mapRequestDtoToItemRequest(itemRequestNewDto);

        return RequestMapper.mapRequestToResponse(requestService.createRequest(itemRequest, userId));
    }

    @GetMapping("/{requestId}")
    @Operation(summary = "Получение запроса по id")
    public ItemResponseDto getRequestById(@RequestHeader(Constants.USER_ID) int userId,
                                          @PathVariable(value = "requestId") int requestId) {
        return requestService.getRequestById(userId, requestId);
    }

    @GetMapping
    @Operation(summary = "Получение всех запросов по id пользователя")
    public List<ItemResponseDto> getAllByOwner(@RequestHeader(Constants.USER_ID) int userId) {
        return requestService.getAllRequestByOwner(userId);
    }

    @GetMapping("/all")
    @Operation(summary = "Получение всех запросов постранично")
    public List<ItemResponseDto> getAllRequest(@RequestHeader(Constants.USER_ID) int userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size) {
        return requestService.getAllRequest(userId, from, size);
    }
}
