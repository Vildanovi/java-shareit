package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(int userId) {
        users.remove(userId);
    }

    @Override
    public User findUserById(int userId) {
//        return Optional.ofNullable(users.get(userId));
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new EntityNotFoundException("Объект не найден: " + userId));
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        User updateUser = users.get(userId);
        if (updateUser != null) {
            updateUser.setName(user.getName());
            updateUser.setEmail(user.getEmail());
        }
        users.put(userId, updateUser);
        return updateUser;
    }

    private Integer getId() {
        int lastId = users.values().stream()
                .mapToInt(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
