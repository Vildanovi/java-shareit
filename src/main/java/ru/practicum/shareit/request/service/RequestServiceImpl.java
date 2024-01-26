package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService{

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequest createRequest(ItemRequest itemRequest, int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        itemRequest.setRequestor(userId);
        itemRequest.setCreated(LocalDateTime.now());
        return requestRepository.save(itemRequest);
    }




}
