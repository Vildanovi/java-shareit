package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityUpdateException;
import ru.practicum.shareit.exception.ValidationBadRequestException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.regex.Pattern;

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
        String email = user.getEmail();
        if (isUniqueEmail(user)) {
            throw new EntityUpdateException("Объект уже существует: " + email);
        }
        if (!isValidEmail(email)) {
            throw new ValidationBadRequestException("Некорректный email: " + email);
        }
        return userRepository.save(user);
    }

    public User putUser(int userId, User user) {
        log.debug("Обновляем пользователя {}", user);
        String name = user.getName();
        String email = user.getEmail();
        User updateUser = userRepository.findUserById(userId);
//        User updateUser = userRepository.findUserById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
        user.setId(userId);
        if (isUniqueEmail(user)) {
            throw new EntityUpdateException("Объект уже существует: " + email);
        }
//        updateUser.setName(user.getName());
//        updateUser.setEmail(user.getEmail());
        if (name != null) {
            updateUser.setName(name);
        }
        if (email != null) {
            if (isValidEmail(email)) {
                updateUser.setEmail(email);
            } else {
                throw new ValidationBadRequestException("Некорректный email: " + email);
            }
        }
        return userRepository.update(updateUser);
    }

    public void deleteUserById(int userId) {
        log.debug("Удаляем пользовател c id: {}", userId);
        userRepository.delete(userId);
        itemRepository.deleteByUserId(userId);
    }

    public Boolean isUniqueEmail(User user) {
        String email = user.getEmail();
        int userId = user.getId();
        boolean checkEmail = false;
        for (User userCheck : userRepository.findAll()) {
            if (userCheck.getEmail().equals(email) && (userCheck.getId() != userId)) {
                checkEmail = true;
                break;
            }
        }
        return checkEmail;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
