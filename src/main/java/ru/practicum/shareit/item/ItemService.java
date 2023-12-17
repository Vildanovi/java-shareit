package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<Item> getItemsByUserId(int userId) {
        return itemRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
    }

//    public Item createItem(int userId, Item item) {
//
//    }
}
