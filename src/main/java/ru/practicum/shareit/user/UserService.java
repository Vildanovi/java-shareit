package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationBadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public List<User> getAllUsers() {
        log.debug("Получаем всех пользователей");
        return userRepository.findAll();
    }

    public User getUser(int userId) {
        log.debug("Заправшиваем пользователя с id: {}", userId);
        return userRepository.findUserById(userId);
//        return userRepository.findUserById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
    }

    public User createUser(User user) {
        log.debug("Создаем пользователя {}", user);
        if (isUniqueEmail(user)) {
            throw new ValidationBadRequestException("Объект уже существует: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public User putUser(int userId, User user) {
        log.debug("Обновляем пользователя {}", user);
        User updateUser = userRepository.findUserById(userId);
//        User updateUser = userRepository.findUserById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        if (isUniqueEmail(user)) {
            throw new ValidationBadRequestException("Объект уже существует: " + user.getEmail());
        }
        updateUser.setName(user.getName());
        updateUser.setEmail(user.getEmail());
        return userRepository.update(updateUser);
    }

    public void deleteUserById(int userId) {
        log.debug("Удаляем пользовател c id: {}", userId);
        userRepository.delete(userId);
        itemRepository.deleteByUserId(userId);
    }

    public Boolean isUniqueEmail(User user) {
        String email = user.getEmail();
//        int userId = user.getId();
        boolean checkEmail = false;
        for (User userCheck : userRepository.findAll()) {
            if (userCheck.getEmail().equals(email)) {
                checkEmail = true;
                break;
            }
        }
        return checkEmail;
    }
}
