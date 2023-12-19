package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User save(User user);

    void delete(int userId);

    User findUserById(int userId);

    User update(User user);
}
