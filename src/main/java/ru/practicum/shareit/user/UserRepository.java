package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> findAll();

    User save(User user);

    void delete(int userId);

    Optional<User> findUserById(int userId);

    User update(User user);
}
